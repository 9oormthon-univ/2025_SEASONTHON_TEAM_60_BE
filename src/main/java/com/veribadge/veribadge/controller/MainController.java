package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.dto.DashboardResponseDto;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "대시보드", description = "메인 페이지 API")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @Operation(summary = "나의 뱃지 관리 조회")
    @GetMapping
    public Response<DashboardResponseDto> getMyBadge(@RequestParam("userId") Long userId){
        DashboardResponseDto dto = mainService.getMyBadge(userId);
        return Response.success(SuccessStatus.MAIN_SUCCESS, dto);
    }
}
