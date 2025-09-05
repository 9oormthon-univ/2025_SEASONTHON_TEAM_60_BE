package com.veribadge.veribadge.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    // COMMON 4XX
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON 400", "파라미터가 올바르지 않습니다."),
    INVALID_BODY(HttpStatus.BAD_REQUEST, "COMMON 400", "요청 본문이 올바르지 않습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON 400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON 401", "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON 403", "금지된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON 404", "찾을 수 없는 리소스입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON 405", "허용되지 않는 HTTP Method입니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER 404", "사용자를 찾을 수 없습니다."),

    // Auth (카카오 로그인 관련 에러 추가)
    KAKAO_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH 500", "카카오 API 연동 중 오류가 발생했습니다."),
    JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH 500", "JSON 파싱 중 오류가 발생했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH 401", "토큰이 유효하지 않습니다."),
    OAUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "AUTH 400", "지원하지 않는 provider입니다."),

    // Verification
    VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "VERI 404", "인증을 찾을 수 없습니다."),
    BADGE_NOT_FOUND(HttpStatus.NOT_FOUND, "VERI 404", "뱃지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}