package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.BadgeLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "badge")
@NoArgsConstructor
@Getter
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BADGE_ID")
    private Long badgeId;

    @OneToOne
    @JoinColumn(name = "VERIFICATION_ID")
    private Verification verificationId;

    private String channelUrl;

    private String verifiedTag;

    @Column(nullable = false)
    private LocalDate verifiedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BadgeLevel badgeLevel;

    @Column(length = 255)
    private String description;

    private String email; // google email

    public Badge(Verification verificationId, LocalDate verifiedDate, BadgeLevel badgeLevel,
                 String channelUrl, String verifiedTag){
        this.verificationId = verificationId;
        this.verifiedDate = verifiedDate;
        this.badgeLevel = badgeLevel;
        this.channelUrl = channelUrl;
        this.verifiedTag = verifiedTag;
    }

    public Badge(Verification verificationId, LocalDate verifiedDate, BadgeLevel badgeLevel){
        this.verificationId = verificationId;
        this.verifiedDate = verifiedDate;
        this.badgeLevel = badgeLevel;
    }

    public void connect(String channelUrl, String verifiedTag, String email, String description) {
        this.channelUrl = channelUrl;
        this.verifiedTag = verifiedTag;
        this.email = email;
        this.description = description;
    }
}
