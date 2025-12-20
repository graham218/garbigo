package com.garbigo.util;

public class EmailTemplates {

    public static String getVerificationTemplate(String appName, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 40px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: #22c55e; padding: 30px; text-align: center; color: white; }
                    .content { padding: 40px; text-align: center; }
                    .button { display: inline-block; padding: 15px 30px; background: #22c55e; color: white; text-decoration: none; border-radius: 8px; font-weight: bold; margin: 20px 0; }
                    .footer { background: #f0f0f0; padding: 20px; text-align: center; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to %s!</h1>
                    </div>
                    <div class="content">
                        <h2>Verify Your Email Address</h2>
                        <p>You're almost ready to start using Garbigo. Please click the button below to verify your email address.</p>
                        <a href="%s" class="button">Verify Email Now</a>
                        <p>If the button doesn't work, copy and paste this link:<br><small>%s</small></p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Garbigo. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, verificationUrl, verificationUrl);
    }

    public static String getPasswordResetTemplate(String appName, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 40px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: #ef4444; padding: 30px; text-align: center; color: white; }
                    .content { padding: 40px; text-align: center; }
                    .button { display: inline-block; padding: 15px 30px; background: #ef4444; color: white; text-decoration: none; border-radius: 8px; font-weight: bold; margin: 20px 0; }
                    .footer { background: #f0f0f0; padding: 20px; text-align: center; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <h2>Reset Your %s Password</h2>
                        <p>We received a request to reset your password. Click the button below to set a new password.</p>
                        <a href="%s" class="button">Reset Password</a>
                        <p>This link will expire in 1 hour for security reasons.</p>
                        <p>If you didn't request this, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Garbigo. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, resetUrl, resetUrl);
    }
}