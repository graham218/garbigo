package com.thepeacemakers.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.frontend-url}")
    private String frontendUrl;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            String verificationLink = frontendUrl + "/verify-email?token=" + token;
            String subject = "Verify Your Garbigo Account";
            
            // Load HTML template
            String htmlTemplate = loadEmailTemplate("email-verification.html");
            
            // Replace placeholders
            htmlTemplate = htmlTemplate.replace("${verificationLink}", verificationLink);
            
            sendHtmlEmail(toEmail, subject, htmlTemplate);
            log.info("Verification email sent to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email");
        }
    }
    
    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            String subject = "Reset Your Garbigo Password";
            
            // Load HTML template
            String htmlTemplate = loadEmailTemplate("password-reset.html");
            
            // Replace placeholders
            htmlTemplate = htmlTemplate.replace("${resetLink}", resetLink);
            
            sendHtmlEmail(toEmail, subject, htmlTemplate);
            log.info("Password reset email sent to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email");
        }
    }
    
    @Async
    public void sendAccountStatusEmail(String toEmail, String subject, String message) {
        try {
            String htmlTemplate = loadEmailTemplate("account-status.html");
            htmlTemplate = htmlTemplate.replace("${message}", message);
            
            sendHtmlEmail(toEmail, subject, htmlTemplate);
            log.info("Account status email sent to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send account status email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send account status email");
        }
    }
    
    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    private String loadEmailTemplate(String templateName) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    }
}