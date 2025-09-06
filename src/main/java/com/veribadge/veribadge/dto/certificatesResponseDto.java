package com.veribadge.veribadge.dto;


import com.veribadge.veribadge.domain.enums.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

@Schema(description = "인증절차 확인 응답 DTO")
public class certificatesResponseDto {
    private VerificationStatus status;

    public certificatesResponseDto(VerificationStatus status) {
        this.status = status;
    }
}
