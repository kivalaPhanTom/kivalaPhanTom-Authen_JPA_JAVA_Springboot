package com.example.userAuthenticate.services;

import com.example.userAuthenticate.dto.request.PermissionRequest;
import com.example.userAuthenticate.dto.response.PermissionResponse;
import com.example.userAuthenticate.entity.Permission;
import com.example.userAuthenticate.mapper.PermissionMapper;
import com.example.userAuthenticate.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
          Permission permission = permissionMapper.toPerrmission(request);
          permission = permissionRepository.save(permission);
          return permissionMapper.toPerrmissionResponse(permission);
    }

    public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPerrmissionResponse).toList();
    }

    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}
