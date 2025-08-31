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
    private VerificationStatus status;

    private String deniedReason;

    public Verification(Member userId, String certificateUrl, LocalDateTime submittedDate, VerificationStatus status){
        this.userId = userId;
        this.certificateUrl = certificateUrl;
        this.submittedDate = submittedDate;
        this.status = status;
    }
}
