package com.veribadge.veribadge.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 이 메소드로 통일하여 사용합니다.
    public Map<String, Object> getUserInfo(String accessToken) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> result = new HashMap<>();

        // 1) People API → 이메일 가져오기
        String peopleApiUrl = "https://people.googleapis.com/v1/people/me?personFields=emailAddresses&access_token=" + accessToken;
        String peopleResponse = restTemplate.getForObject(peopleApiUrl, String.class);
        JsonNode peopleJson = objectMapper.readTree(peopleResponse);
        String email = peopleJson.path("emailAddresses").get(0).path("value").asText();

        result.put("email", email);

        // 2) YouTube API → 채널 정보 가져오기
        String youtubeApiUrl = "https://www.googleapis.com/youtube/v3/channels?part=snippet&mine=true&access_token=" + accessToken;
        String youtubeResponse = restTemplate.getForObject(youtubeApiUrl, String.class);
        JsonNode youtubeJson = objectMapper.readTree(youtubeResponse);

        if (youtubeJson.has("items") && youtubeJson.get("items").size() > 0) {
            JsonNode channel = youtubeJson.get("items").get(0);
            String channelId = channel.path("id").asText();
            // customUrl은 없을 수 있으므로 snippet에 있는지 확인 후 가져옵니다.
            String customUrl = channel.path("snippet").has("customUrl")
                    ? channel.path("snippet").path("customUrl").asText()
                    : "";

            String channelLink = !customUrl.isEmpty()
                    ? "https://www.youtube.com/" + customUrl
                    : "https://www.youtube.com/channel/" + channelId;

            result.put("channelLink", channelLink);
        } else {
            // 유튜브 채널이 없는 경우에 대한 처리
            result.put("channelLink", null);
        }

        return result;
    }
}