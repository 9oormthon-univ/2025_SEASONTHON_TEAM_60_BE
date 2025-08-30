package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
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
    private VerificationStatus status;

    private String deniedReason;

    public Verification(String certificateUrl, VerificationStatus status){
        this.certificateUrl = certificateUrl;
        this.status = status;
    }
}
