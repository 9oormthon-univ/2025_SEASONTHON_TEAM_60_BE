package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.dto.DashboardResponseDto;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
@Tag(name = "대시보드", description = "메인 페이지 API")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;


    @Operation(
            summary = "나의 뱃지 관리 조회",
            description = "메인 대시보드에서 사용자의 뱃지 상태, 유튜브 채널 연동 정보, 인증 현황 등 종합적인 정보를 조회합니다."
    )
    @GetMapping
    public Response<DashboardResponseDto> getMyBadge(){
        DashboardResponseDto dto = mainService.getMyBadge();
        return Response.success(SuccessStatus.MAIN_SUCCESS, dto);
    }
}
