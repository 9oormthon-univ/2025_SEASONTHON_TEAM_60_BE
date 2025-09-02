package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false)
    private String username;

    @Column(unique = true)
    private String email;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
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