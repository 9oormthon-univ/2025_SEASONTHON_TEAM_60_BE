package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(nullable = false, unique = true)
    private Long kakaoId; // FIXME : 나중에 socialId로 변경하고 socialType 객체 생성하기

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String username;


    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Member(Long kakaoId, String email, String username, Role role, LocalDateTime createdAt){
        this.kakaoId = kakaoId;
        this.email = email;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
    }


    public void updateEmail(String email) {
        this.email = email;
    }

}
