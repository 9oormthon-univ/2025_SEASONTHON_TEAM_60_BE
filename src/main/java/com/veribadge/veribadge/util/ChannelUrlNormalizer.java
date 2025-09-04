package com.veribadge.veribadge.util;

import org.springframework.stereotype.Component;

@Component
public class ChannelUrlNormalizer {

    public String normalize(String inputUrl) {
        if (inputUrl == null || inputUrl.isBlank()) {
            return null;
        }

        // 1. @handle 추출
        String handle = null;
        if (inputUrl.contains("@")) {
            int startIndex = inputUrl.indexOf("@");
            handle = inputUrl.substring(startIndex);
            // 쿼리 파라미터 제거
            int queryIndex = handle.indexOf("?");
            if (queryIndex != -1) {
                handle = handle.substring(0, queryIndex);
            }
        }

        if (handle == null || handle.isBlank()) {
            return null;
        }

        // 2. 원하는 형식으로 변환
        return "www.youtube.com/" + handle;
    }
}
