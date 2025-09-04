package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String username;


    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Member(Long kakaoId, String username) {
        this.kakaoId = kakaoId;
        this.username = username;
        this.role = Role.USER;
        this.createdAt = LocalDateTime.now();
    }


    public void updateEmail(String email) {
        this.email = email;
    }

}
