package com.kharlamova.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credentials")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Credential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique=true, nullable=false)
    private Long userId;

    @Column(unique=true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
