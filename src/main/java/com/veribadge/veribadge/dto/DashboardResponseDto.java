package com.veribadge.veribadge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.veribadge.veribadge.domain.enums.BadgeLevel;
import com.veribadge.veribadge.domain.enums.Role;
import com.veribadge.veribadge.domain.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DashboardResponseDto {
    private String username;
    private Role role;
    private VerificationStatus status;

    // 뱃지 발급받은 경우만
    private BadgeLevel badgeLevel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate badgeDate;

    public DashboardResponseDto(String username, Role role, VerificationStatus status){
        this.username = username;
        this.role = role;
        this.status = status;
    }
}
