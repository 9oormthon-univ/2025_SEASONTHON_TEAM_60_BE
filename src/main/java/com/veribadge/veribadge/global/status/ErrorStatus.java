package com.veribadge.veribadge.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    // COMMON 4XX
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON400_1", "파라미터가 올바르지 않습니다."),
    INVALID_BODY(HttpStatus.BAD_REQUEST, "COMMON400_2", "요청 본문이 올바르지 않습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400_3", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "찾을 수 없는 리소스입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않는 HTTP Method입니다."),

    // Auth (카카오 로그인 관련 에러 추가)
    KAKAO_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH5001", "카카오 API 연동 중 오류가 발생했습니다."),
    JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH5002", "JSON 파싱 중 오류가 발생했습니다."),

    // Verification

    VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "VERI 404", "인증을 찾을 수 없습니다."),

    // Member (USER_NOT_FOUND 삭제 및 MEMBER_NOT_FOUND 코드를 더 명확하게 수정)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}