package com.example.userAuthenticate.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//để đánh dấu class này là 1 table, dùng @Entity
@Entity
public class Role {
     @Id
     String name;

     String description;
     @ManyToMany //biểu diễn mối quan hệ nhiều - nhiều giữa role và permission
     Set<Permission> permissions; //sau khi thêm dòng này và chạy lại chương trình, thì nó tự tạo bảng role_permisson
}
