package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.domain.enums.BadgeLevel;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "관리자 페이지", description = "관리자 API")
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    // 관리자 페이지 get API
//    @GetMapping
//    public Response<Object> getAdminPage(){
//        // TODO : 관리자페이지 실제로 개발해야 함
//        return Response.success(SuccessStatus.SUCCESS, null);
//    }

    @Operation(summary = "인증서 수락")
    @PostMapping("/admit/{userId}")
    public Response<Object> admitVerification(@PathVariable("userId") Long userId,
                                              @RequestParam("BadgeLevel") BadgeLevel badgeLevel){
        adminService.admitVerification(userId, badgeLevel);
        return Response.success(SuccessStatus.SUCCESS, null); // TODO : 성공 http 수정
    }

    @Operation(summary = "인증서 거절")
    @PostMapping("/reject/{userId}")
    public Response<Object> rejectVerification(@PathVariable("userId") Long userId,
                                               @RequestParam("deniedReason") String deniedReason){
        adminService.rejectVerification(userId, deniedReason);
        return Response.success(SuccessStatus.SUCCESS, null); // TODO : 성공 http 수정
    }
}
