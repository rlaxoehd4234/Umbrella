package com.umbrella.config.mail;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "*"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders(
                        "Accept",
                        "Accept-Encoding",
                        "Accept-Language",
                        "Access-Control-Request-Headers",
                        "Access-Control-Request-Method",
                        "Connection",
                        "Host",
                        "Origin",
                        "Referer",
                        "Sec-Fetch-Mode",
                        "User-Agent",
                        "Authorization"
                )
                .exposedHeaders(
                        "Authorization"
                )
                .allowCredentials(true)
                .maxAge(3000);
    }
}
