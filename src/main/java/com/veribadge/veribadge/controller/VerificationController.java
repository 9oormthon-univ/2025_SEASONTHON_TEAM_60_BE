package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.dto.UploadVerificationResponseDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certificates")
public class VerificationController {

    private final VerificationService verificationService;

    @Operation(
            summary = "소득 증명 파일 업로드",
            description = "JPG 또는 PNG 형식의 소득 증명 파일을 업로드합니다. 최대 10MB까지 지원하며, 인증 요청은 자동으로 제출됩니다."
    )
    @PostMapping(
            value = "/income/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response<UploadVerificationResponseDto>> uploadIncomeCertificate(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        validateFile(file);

        UploadVerificationResponseDto body =
                verificationService.processIncomeCertificateUpload(file, userId);

        return ResponseEntity.ok(Response.success(SuccessStatus.VERIFICATION_SUBMITTED, body));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorStatus.FILE_EMPTY);
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png")
                        || contentType.equals("image/jpeg")
                        || contentType.equals("image/jpg"))) {
            throw new CustomException(ErrorStatus.FILE_TYPE_NOT_SUPPORTED);
        }
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new CustomException(ErrorStatus.FILE_SIZE_EXCEEDED);
        }
    }
}
