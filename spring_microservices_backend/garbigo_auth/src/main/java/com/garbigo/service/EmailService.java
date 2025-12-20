package com.garbigo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.garbigo.util.EmailTemplates;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.name}")
    private String appName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendVerificationEmail(String to, String token) throws MessagingException {
        String subject = "Verify your " + appName + " account";
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;

        String htmlContent = EmailTemplates.getVerificationTemplate(appName, verificationUrl);

        sendHtmlEmail(to, subject, htmlContent);
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        String subject = "Reset your " + appName + " password";
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        String htmlContent = EmailTemplates.getPasswordResetTemplate(appName, resetUrl);

        sendHtmlEmail(to, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}