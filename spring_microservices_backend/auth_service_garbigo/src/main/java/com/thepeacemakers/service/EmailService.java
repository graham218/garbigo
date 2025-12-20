package com.thepeacemakers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.name}")
    private String appName;
    
    @Async
    public void sendVerificationEmail(String to, String token) {
        try {
            String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;
            
            Context context = new Context();
            context.setVariable("verificationUrl", verificationUrl);
            context.setVariable("appName", appName);
            
            String htmlContent = templateEngine.process("verify-email", context);
            
            sendEmail(to, "Verify Your Email - " + appName, htmlContent);
            
        } catch (Exception e) {
            log.error("Failed to send verification email: ", e);
        }
    }
    
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            String resetUrl = baseUrl + "/reset-password?token=" + token;
            
            Context context = new Context();
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("appName", appName);
            
            String htmlContent = templateEngine.process("reset-password", context);
            
            sendEmail(to, "Reset Your Password - " + appName, htmlContent);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email: ", e);
        }
    }
    
    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("appName", appName);
            context.setVariable("loginUrl", baseUrl + "/login");
            
            String htmlContent = templateEngine.process("welcome-email", context);
            
            sendEmail(to, "Welcome to " + appName + "!", htmlContent);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email: ", e);
        }
    }
    
    @Async
    public void sendAccountActivatedEmail(String to) {
        try {
            Context context = new Context();
            context.setVariable("appName", appName);
            context.setVariable("loginUrl", baseUrl + "/login");
            
            String htmlContent = templateEngine.process("account-activated", context);
            
            sendEmail(to, "Account Activated - " + appName, htmlContent);
            
        } catch (Exception e) {
            log.error("Failed to send account activated email: ", e);
        }
    }
    
    @Async
    public void sendAccountDeactivatedEmail(String to) {
        try {
            Context context = new Context();
            context.setVariable("appName", appName);
            context.setVariable("supportEmail", "support@" + appName.toLowerCase() + ".com");
            
            String htmlContent = templateEngine.process("account-deactivated", context);
            
            sendEmail(to, "Account Deactivated - " + appName, htmlContent);
            
        } catch (Exception e) {
            log.error("Failed to send account deactivated email: ", e);
        }
    }
    
    @Async
    public void sendAccountSuspendedEmail(String to) {
        try {
            Context context = new Context();
            context.setVariable("appName", appName);
            context.setVariable("supportEmail", "support@" + appName.toLowerCase() + ".com");
            
            String htmlContent = templateEngine.process("account-suspended", context);
            
            sendEmail(to, "Account Suspended - " + appName, htmlContent);
            
        } catch (Exception e) {
            log.error("Failed to send account suspended email: ", e);
        }
    }
    
    @Async
    public void sendAccountBlockedEmail(String to) {
        try {
            Context context = new Context();
            context.setVariable("appName", appName);
            context.setVariable("supportEmail", "support@" + appName.toLowerCase() + ".com");
            
            String htmlContent = templateEngine.process("account-blocked", context);
            
            sendEmail(to, "Account Blocked - " + appName, htmlContent);
            
        } catch (Exception e) {
            log.error("Failed to send account blocked email: ", e);
        }
    }
    
    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail, appName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            log.info("Email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: ", to, e);
            throw new RuntimeException("Failed to send email");
        }
    }
}