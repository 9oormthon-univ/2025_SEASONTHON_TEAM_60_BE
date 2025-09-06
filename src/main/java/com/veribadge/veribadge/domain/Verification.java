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

    @OneToOne//추후 파일 업로드 재요청 들어오면 바꿔야 할 수 있음
    @JoinColumn(name = "USER_ID")
    private Member userId;

    // 파일 저장 안 하면 null 가능하도록 (임시 URL/외부 URL 들어갈 수 있게)
    @Column(nullable = true)
    private String certificateUrl;

    @Column(nullable = false)
    private LocalDateTime submittedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    private String deniedReason;

    private String description;

    @Column(nullable = false, unique = true, length = 64)
    private String fileId;

    @Column(nullable = false)
    private String fileName;

    public Verification(Member userId, String fileId, String fileName, String certificateUrl){
        this.userId = userId;
        this.fileId = fileId;
        this.fileName = fileName;
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
