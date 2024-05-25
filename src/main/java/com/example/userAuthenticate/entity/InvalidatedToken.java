package com.example.userAuthenticate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//để đánh dấu class này là 1 table, dùng @Entity
@Entity
public class InvalidatedToken {
    @Id
     String id;
     Date expiryTime;
}
