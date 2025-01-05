package com.example.backend.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender emailSender, 
                       @Value("${spring.mail.username}") String fromEmail) {
        this.emailSender = emailSender;
        this.fromEmail = fromEmail;
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            String resetLink = "http://your-frontend-url/reset-password?token=" + token;
            String emailContent = buildPasswordResetEmailTemplate(resetLink);

            helper.setText(emailContent, true); // true indicates HTML content

            emailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildPasswordResetEmailTemplate(String resetLink) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2>Password Reset Request</h2>
                    <p>Hello,</p>
                    <p>We received a request to reset your password. Click the button below to create a new password:</p>
                    <p style="text-align: center; margin: 30px 0;">
                        <a href="%s" 
                           style="background-color: #4CAF50; 
                                  color: white; 
                                  padding: 12px 24px; 
                                  text-decoration: none; 
                                  border-radius: 4px;">
                            Reset Password
                        </a>
                    </p>
                    <p>If you didn't request this password reset, you can safely ignore this email.</p>
                    <p>This link will expire in 1 hour for security reasons.</p>
                    <p>Best regards,<br>Your Application Team</p>
                </div>
            </body>
            </html>
            """.formatted(resetLink);
    }

   /* public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    } */

}

