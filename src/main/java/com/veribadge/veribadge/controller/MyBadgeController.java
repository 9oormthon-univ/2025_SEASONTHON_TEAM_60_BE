package com.veribadge.veribadge.controller;

import com.veribadge.veribadge.dto.MyBadgeResponseDto;
import com.veribadge.veribadge.exception.Response;
import com.veribadge.veribadge.global.status.SuccessStatus;
import com.veribadge.veribadge.service.MyBadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("my-badge")
@Tag(name = "나의 뱃지 관리", description = "나의 뱃지 관리 API")
@RequiredArgsConstructor
public class MyBadgeController {

    private final MyBadgeService myBadgeService;


    @Operation(
            summary = "나의 뱃지 상세 정보 조회",
            description = "로그인한 사용자의 뱃지 발급 여부, 인증 태그, 인증 날짜, 채널 URL 등의 상세 정보를 조회합니다."
    )
    @GetMapping
    public Response<MyBadgeResponseDto> getMyBadge(){
        MyBadgeResponseDto dto = myBadgeService.getMyBadge();
        return Response.success(SuccessStatus.MY_BADGE_SUCCESS, dto);
    }

//    @Operation(summary = "Youtube 채널 연결 및 고유 태그 발급")
//    @PostMapping("/connect-url")
//    public Response<Object> connectChannel(@RequestParam("channelUrl") String channelUrl,
//                                           @RequestParam("email")){
//        String badgeTag = myBadgeService.connectChannel(channelUrl, email);
//        return Response.success(SuccessStatus.CHANNEL_CONNECTED, badgeTag);
//    }
}
