package com.umbrella.controller;

import com.umbrella.dto.email.EmailAuthResponseDto;
import com.umbrella.dto.email.EmailPostRequestDto;
import com.umbrella.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final EmailService emailService;

    private static final String SUBJECT = "[Umbrella] 인증을 위한 인증 코드 발급";

    @PostMapping("/send-email")
    public ResponseEntity sendEmail(@RequestBody EmailPostRequestDto emailPostRequestDto) {
        String authKey = emailService.sendAuthEmail(emailPostRequestDto, SUBJECT);
        EmailAuthResponseDto emailAuthResponseDto = EmailAuthResponseDto.builder()
                .authKey(authKey)
                .build();

        return ResponseEntity.ok(emailAuthResponseDto);
    }
}
