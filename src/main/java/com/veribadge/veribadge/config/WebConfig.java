package com.veribadge.veribadge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 개발 중엔 패턴으로 허용 (확장프로그램 ID 변화/서브패스 대응)
                .allowedOriginPatterns(
                        "https://*.youtube.com",
                        "https://www.youtube.com",
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "chrome-extension://*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

