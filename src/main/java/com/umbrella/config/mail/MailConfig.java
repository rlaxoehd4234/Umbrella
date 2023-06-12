package com.umbrella.config.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    private static final String HOST = "smtp.gmail.com";
    private static final int PORT  = 587;
    private static final String EMAIL_FOR_SEND = "umbrella.on.your.hand@gmail.com";
    private static final String APP_PASSWORD_FOR_SMTP = "iudfdxwjtxmuhwpt";
    private static final String TIMEOUT = "5000";

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(HOST);
        javaMailSender.setPort(PORT);
        javaMailSender.setUsername(EMAIL_FOR_SEND);
        javaMailSender.setPassword(APP_PASSWORD_FOR_SMTP);

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();

        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.ssl.trust",HOST);
        properties.setProperty("mail.smtp.connection-timeout", TIMEOUT);
        properties.setProperty("mail.smtp.timeout", TIMEOUT);
        properties.setProperty("mail.smtp.write-timeout", TIMEOUT);
        properties.setProperty("mail.smtp.ssl.enable","true");

        return properties;
    }
}
