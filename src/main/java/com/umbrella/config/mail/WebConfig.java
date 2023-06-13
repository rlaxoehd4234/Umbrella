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
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("*")
//                .allowedHeaders("*")
//                .exposedHeaders("*")
//                .allowCredentials(false)
//                .maxAge(3000);
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000","https://our-umbrella.vercel.app/")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3000);
    }


}
