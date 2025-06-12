package com.inqool.tennisclub.data.model;

import com.inqool.tennisclub.data.model.enums.AuthorizationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_table")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "auth_type", nullable = false)
    private AuthorizationType authType;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
