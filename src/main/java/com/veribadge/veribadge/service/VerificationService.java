package com.veribadge.veribadge.service;

import com.veribadge.veribadge.domain.Member;
import com.veribadge.veribadge.domain.Verification;
import com.veribadge.veribadge.domain.enums.VerificationStatus;
import com.veribadge.veribadge.dto.UploadVerificationResponseDto;
import com.veribadge.veribadge.exception.CustomException;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.repository.MemberRepository;
import com.veribadge.veribadge.repository.VerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final MemberRepository memberRepository;
    private final VerificationRepository verificationRepository;

    @Transactional
    public UploadVerificationResponseDto processIncomeCertificateUpload(MultipartFile file, Long userId) {
        // 1) 사용자
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2) 유효성 (이미지 전용)
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
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new CustomException(ErrorStatus.FILE_SIZE_EXCEEDED);
        }

        // 3) 파일은 저장하지 않음 → 식별자/파일명만
        String fileId = "file_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String fileName = (file.getOriginalFilename() != null) ? file.getOriginalFilename() : "income_certificate.png";

        // 4) certificateUrl은 저장 안 하므로 null 또는 임시값
        String certificateUrl = null; // or: "temp://" + fileId + "/" + fileName

        // 5) 엔티티 저장
        Verification verification = new Verification(member, fileId, fileName, certificateUrl);
        verificationRepository.save(verification);

        // 6) 응답
        return UploadVerificationResponseDto.of(fileName, fileId, VerificationStatus.SUBMITTED);
    }
}
