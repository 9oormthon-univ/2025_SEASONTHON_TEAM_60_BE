package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VERIFICATION_ID")
    private Long verificationId;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private Member userId;

    @Column(nullable = false)
    private String certificateUrl;

    @Column(nullable = false)
    private LocalDateTime submittedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    private String deniedReason;

    private String description;

    public Verification(Member userId, String certificateUrl){
        this.userId = userId;
        this.certificateUrl = certificateUrl;
        this.submittedDate = LocalDateTime.now();
        this.status = VerificationStatus.SUBMITTED;
    }

    public void admitVerification(Member userId, String description){
        this.userId = userId;
        this.description = description;
        this.status = VerificationStatus.VERIFIED;
    }
    public void rejectVerification(Member userId, String deniedReason){
        this.userId = userId;
        this.deniedReason = deniedReason;
        this.status = VerificationStatus.REJECTED;
    }
}
