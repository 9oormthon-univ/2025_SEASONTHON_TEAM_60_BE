package com.veribadge.veribadge.dto;

import com.veribadge.veribadge.domain.enums.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "파일 업로드 응답 DTO")
public class UploadVerificationResponseDto {

    private String fileName;
    private String fileId;
    private VerificationStatus status;

    public static UploadVerificationResponseDto of(String fileName, String fileId, VerificationStatus status) {
        return new UploadVerificationResponseDto(fileName, fileId, status);
    }
}
