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
                .allowedOriginPatterns(
                        "http://ec2-3-39-93-217.ap-northeast-2.compute.amazonaws.com:8800",
                        "http://localhost:3000",
                        "https://our-umbrella.vercel.app/"
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
