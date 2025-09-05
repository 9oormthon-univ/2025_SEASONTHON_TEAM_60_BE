package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private Long kakaoId; // FIXME : 나중에 socialId로 변경하고 socialType 객체 생성하기

    @Column(nullable = false)
    private String username;


    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Member(Long kakaoId, String username, Role role) {
        this.kakaoId = kakaoId;
        this.username = username;
        this.role = role;
    }

    public static Member createUser(Long kakaoId, String username) {
        return new Member(kakaoId, username, Role.USER);
    }

    public static Member createAdmin(Long kakaoId, String username) {
        return new Member(kakaoId, username, Role.ADMIN);
    }
}
