package com.umbrella.service;

import com.umbrella.dto.email.EmailPostRequestDto;

public interface EmailService {

    String sendAuthEmail(EmailPostRequestDto emailPostRequestDto, String subject);

    String createAuthCode();

    String setContext(String authKey);
}
