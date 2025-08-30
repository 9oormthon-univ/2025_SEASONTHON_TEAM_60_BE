package com.veribadge.veribadge.domain;

import com.veribadge.veribadge.domain.enums.BadgeLevel;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BADGE_ID")
    private Long badgeId;

    @OneToOne
    @JoinColumn(name = "VERIFICATION_ID")
    private Verification verificationId;

    private String channelUrl;

    @Column(nullable = false)
    private String verifiedTag;

    @Column(nullable = false)
    private LocalDate verifiedDate;

    @Column(nullable = false)
    private BadgeLevel badgeLevel;

    private Badge(String verifiedTag, LocalDate verifiedDate, BadgeLevel badgeLevel){
        this.verifiedTag = verifiedTag;
        this.verifiedDate = verifiedDate;
        this.badgeLevel = badgeLevel;
    }
}
