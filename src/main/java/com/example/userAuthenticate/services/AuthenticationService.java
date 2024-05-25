package com.example.userAuthenticate.services;

import com.example.userAuthenticate.dto.request.AuthenticationRequest;
import com.example.userAuthenticate.dto.request.IntrospectRequest;
import com.example.userAuthenticate.dto.request.LogoutRequest;
import com.example.userAuthenticate.dto.request.RefreshRequest;
import com.example.userAuthenticate.dto.response.AuthenticationResponse;
import com.example.userAuthenticate.dto.response.IntrospectResponse;
import com.example.userAuthenticate.entity.InvalidatedToken;
import com.example.userAuthenticate.entity.User;
import com.example.userAuthenticate.exception.AppException;
import com.example.userAuthenticate.exception.ErrorCode;
import com.example.userAuthenticate.repository.InValidatedTokenRepository;
import com.example.userAuthenticate.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.nimbusds.jose.JWSVerifier;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor // sẽ sinh ra một constructor với các tham số bắt buộc phải có giá trị.
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//nếu các field bên trong class ko xac định phạm vi, thì sẽ mặc định là private hết,đồng thời, field đó có kiểu là final
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    UserRepository userRepository;
    InValidatedTokenRepository inValidatedTokenRepository;

    @NonFinal //đây là 1 anotation của lombok, giúp ko inject vào constructor
    @Value("${jwt.signerKey}") //đọc biến từ file application.properties
    protected String SIGNER_KEY;
    //protected static final String SIGNER_KEY = "CW780v1HLtaZvVw4ZOoy6Gj6axzhQKJjQJB4BZs8PiX2gdDPvmyxutTZZNZNBjaT";   // lên trang này để sinh ra key: https://generate-random.org/encryption-key-generator

    @NonFinal //đây là 1 anotation của lombok, giúp ko inject vào constructor
    @Value("${jwt.valid-duration}") //đọc biến từ file application.properties
    protected Long VALID_DURATION;

    @NonFinal //đây là 1 anotation của lombok, giúp ko inject vào constructor
    @Value("${jwt.refreshable-duration}") //đọc biến từ file application.properties
    protected Long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try{
            verifyToken(token, false);
        }catch (AppException e){
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
    public void logout(LogoutRequest request) throws ParseException, JOSEException{
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expirtyTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expirtyTime)
                    .build();
            inValidatedTokenRepository.save(invalidatedToken);
        }catch (AppException exception){
            log.info("Token already expire");
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        //nếu có tín hiệu refreshtoken, thì verify refresh token, còn nếu ko có, thì verify token
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if(!( verified && expiryTime.after(new Date())))
           throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(inValidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
          throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    private String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("duy821999")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli())) //thời gian sống của token là nằm ở biến VALID_DURATION
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try{
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        }catch (JOSEException e){
             throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request)  throws JOSEException, ParseException {
       var signedJWT = verifyToken(request.getToken(), true);
       var jit = signedJWT.getJWTClaimsSet().getJWTID();
       var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        inValidatedTokenRepository.save(invalidatedToken);
        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user  = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.UNAUTHENTICATED));
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role->{
                stringJoiner.add("ROLE_"+ role.getName());
                if(CollectionUtils.isEmpty(role.getPermissions())){
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
                };
            });
            //hoặc có thể viết: user.getRoles().forEach(s -> stringJoiner.add(s));
        }
        return stringJoiner.toString();
    }
}
