package com.umbrella.service.Impl;

import com.umbrella.dto.email.EmailPostRequestDto;
import com.umbrella.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SpringTemplateEngine springTemplateEngine;
    private final JavaMailSender javaMailSender;

    @Override
    public String sendAuthEmail(EmailPostRequestDto emailPostRequestDto, String subject) {
        String authKey = createAuthCode();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setFrom("umbrella.on.your.hand@gmail.com");
            mimeMessageHelper.setTo(emailPostRequestDto.getEmail());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(setContext(authKey), true);
            javaMailSender.send(mimeMessage);

            return authKey;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createAuthCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0: key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1: key.append((char) ((int) random.nextInt(26) + 65)); break;
                default: key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    @Override
    public String setContext(String authKey) {
        Context context = new Context();
        context.setVariable("authKey", authKey);
        return springTemplateEngine.process("email", context);
    }
}
