package com.example.userAuthenticate.repository;

import com.example.userAuthenticate.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InValidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
