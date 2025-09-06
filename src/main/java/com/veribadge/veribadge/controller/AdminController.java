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

import java.util.Optional;

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

    @Operation(summary = "인증서 수락",
               description = "인증서 파일이 소득 증명인 경우 설명칸을 비워두고, 의사 증명인 경우 병원, 전공에 대한 설명을 적어주세요." +
                       " ex) 서울아산병원 심장내과 전문의")
    @PostMapping("/admit/{userId}")
    public Response<Object> admitVerification(@PathVariable("userId") Long userId,
                                              @RequestParam("badgeLevel") BadgeLevel badgeLevel,
                                              @RequestParam("description") Optional<String> description){
        adminService.admitVerification(userId, badgeLevel, description.orElse(null));
        return Response.success(SuccessStatus.SUCCESS, null); // TODO : 성공 http 수정
    }

    @Operation(
            summary = "인증서 거절",
            description = "특정 사용자의 인증 요청을 거절합니다. 거절 사유(deniedReason)를 함께 입력해야 하며, 해당 사유는 사용자에게 전달됩니다."
    )
    @PostMapping("/reject/{userId}")
    public Response<Object> rejectVerification(@PathVariable("userId") Long userId,
                                               @RequestParam("deniedReason") String deniedReason){
        adminService.rejectVerification(userId, deniedReason);
        return Response.success(SuccessStatus.SUCCESS, null); // TODO : 성공 http 수정
    }
}
