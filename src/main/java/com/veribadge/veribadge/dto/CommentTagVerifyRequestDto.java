package com.veribadge.veribadge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentTagVerifyRequestDto {
    private String tag;         // ex: "@veri-gold-ab12cd"
    private String channelUrl;  // ex: "www.youtube.com/@<유튜브채널명>"
}
