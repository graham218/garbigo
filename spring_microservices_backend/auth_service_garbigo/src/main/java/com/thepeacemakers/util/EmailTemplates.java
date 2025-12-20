package com.thepeacemakers.util;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplates {
    
    public static final String EMAIL_VERIFICATION_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Verify Your Email - Garbigo</title>
            <style>
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    line-height: 1.6;
                    color: #333;
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                }
                .header {
                    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
                    color: white;
                    padding: 30px;
                    text-align: center;
                    border-radius: 10px 10px 0 0;
                }
                .content {
                    background: #f9f9f9;
                    padding: 30px;
                    border-radius: 0 0 10px 10px;
                }
                .button {
                    display: inline-block;
                    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
                    color: white;
                    text-decoration: none;
                    padding: 12px 30px;
                    border-radius: 25px;
                    font-weight: bold;
                    margin: 20px 0;
                }
                .footer {
                    text-align: center;
                    margin-top: 30px;
                    color: #666;
                    font-size: 12px;
                }
                .logo {
                    font-size: 28px;
                    font-weight: bold;
                    margin-bottom: 10px;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="logo">♻️ Garbigo</div>
                <h1>Verify Your Email Address</h1>
            </div>
            <div class="content">
                <p>Hello,</p>
                <p>Thank you for registering with <strong>Garbigo</strong> - your smart garbage collection partner!</p>
                <p>To complete your registration and start using our services, please verify your email address by clicking the button below:</p>
                
                <div style="text-align: center;">
                    <a href="${verificationLink}" class="button">Verify Email Address</a>
                </div>
                
                <p>This verification link will expire in <strong>24 hours</strong>.</p>
                <p>If you didn't create an account with Garbigo, you can safely ignore this email.</p>
                
                <p>Best regards,<br>The Garbigo Team</p>
            </div>
            <div class="footer">
                <p>© 2024 Garbigo. All rights reserved.</p>
                <p>This email was sent to you because you registered on Garbigo.</p>
                <p>If you have any questions, contact us at support@garbigo.com</p>
            </div>
        </body>
        </html>
        """;
    
    public static final String PASSWORD_RESET_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Reset Your Password - Garbigo</title>
            <style>
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    line-height: 1.6;
                    color: #333;
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                }
                .header {
                    background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
                    color: white;
                    padding: 30px;
                    text-align: center;
                    border-radius: 10px 10px 0 0;
                }
                .content {
                    background: #f9f9f9;
                    padding: 30px;
                    border-radius: 0 0 10px 10px;
                }
                .button {
                    display: inline-block;
                    background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
                    color: white;
                    text-decoration: none;
                    padding: 12px 30px;
                    border-radius: 25px;
                    font-weight: bold;
                    margin: 20px 0;
                }
                .footer {
                    text-align: center;
                    margin-top: 30px;
                    color: #666;
                    font-size: 12px;
                }
                .logo {
                    font-size: 28px;
                    font-weight: bold;
                    margin-bottom: 10px;
                }
                .warning {
                    background: #fff3cd;
                    border-left: 4px solid #ffc107;
                    padding: 15px;
                    margin: 20px 0;
                    border-radius: 4px;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="logo">♻️ Garbigo</div>
                <h1>Reset Your Password</h1>
            </div>
            <div class="content">
                <p>Hello,</p>
                <p>We received a request to reset your password for your <strong>Garbigo</strong> account.</p>
                
                <div class="warning">
                    <p><strong>Note:</strong> If you didn't request a password reset, you can safely ignore this email.</p>
                </div>
                
                <p>To reset your password, click the button below:</p>
                
                <div style="text-align: center;">
                    <a href="${resetLink}" class="button">Reset Password</a>
                </div>
                
                <p>This password reset link will expire in <strong>30 minutes</strong> for security reasons.</p>
                
                <p>For security, please:</p>
                <ul>
                    <li>Never share your password with anyone</li>
                    <li>Use a strong, unique password</li>
                    <li>Enable two-factor authentication for added security</li>
                </ul>
                
                <p>Best regards,<br>The Garbigo Team</p>
            </div>
            <div class="footer">
                <p>© 2024 Garbigo. All rights reserved.</p>
                <p>This email was sent to you because you requested a password reset.</p>
                <p>If you have any questions, contact us at support@garbigo.com</p>
            </div>
        </body>
        </html>
        """;
}