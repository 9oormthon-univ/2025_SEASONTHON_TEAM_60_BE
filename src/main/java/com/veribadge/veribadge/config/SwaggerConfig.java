package com.veribadge.veribadge.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
//@EnableWebMvc
public class SwaggerConfig {
    // http://localhost:8080/swagger-ui/index.html

    @Bean
    public OpenAPI customOpenAPI() {

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("JSESSIONID");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("cookieAuth");
        Info info = new Info()
                        .title("VeriBadge API")
                        .description("베리뱃지 프로젝트의 REST API 명세서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("VeriBadge Team")
                                .email("ysnoh0455@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0"));

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("cookieAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(info);
    }
}