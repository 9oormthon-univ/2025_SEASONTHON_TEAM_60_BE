package com.veribadge.veribadge.dto;

import com.veribadge.veribadge.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "로그인 성공 응답 데이터")
public class LoginResponseDto {
    @Schema(description = "우리 서비스의 자체 회원 ID", example = "1")
    private Long userId;

    @Schema(description = "카카오에서 발급받은 ID", example = "1")
    private Long kakaoId;

    @Schema(description = "사용자 실명", example = "홍길동")
    private String username;

    @Schema(description = "로그인 성공 메시지", example = "로그인에 성공했습니다.")
    private String message;

    public LoginResponseDto(Member member) {
        this.userId = member.getUserId();
        this.username = member.getUsername();
        this.kakaoId = member.getKakaoId();
        this.message = "로그인에 성공했습니다.";
    }


}
