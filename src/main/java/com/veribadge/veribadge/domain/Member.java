package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Member(String email, String username, String password, Role role, LocalDateTime createdAt){
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }
}
