package com.example.userAuthenticate.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;
import java.time.LocalDate;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//để đánh dấu class này là 1 table, dùng @Entity
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String id;
     String username;
     String password;
     String firstName;
     String lastName;
     LocalDate dob;
     @ManyToMany //biểu diễn mối quan hệ nhiều - nhiều giữa User và Role
     Set<Role> roles; //sau khi thêm dòng này và chạy lại chương trình, thì nó tự tạo bảng user_role
}
