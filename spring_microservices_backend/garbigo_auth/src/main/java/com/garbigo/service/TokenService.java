package com.garbigo.service;

import com.garbigo.model.PasswordResetToken;
import com.garbigo.model.User;
import com.garbigo.model.VerificationToken;
import com.garbigo.repository.PasswordResetTokenRepository;
import com.garbigo.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TokenService {

    private static final long VERIFICATION_EXPIRY_HOURS = 24;
    private static final long RESET_EXPIRY_HOURS = 1;

    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public TokenService(VerificationTokenRepository verificationTokenRepository,
                        PasswordResetTokenRepository passwordResetTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public VerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .userId(user.getId())
                .expiryDate(Instant.now().plusSeconds(VERIFICATION_EXPIRY_HOURS * 3600))
                .build();

        verificationTokenRepository.deleteByUserId(user.getId());
        return verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElse(null);
    }

    public void deleteVerificationToken(VerificationToken token) {
        verificationTokenRepository.delete(token);
    }

    public PasswordResetToken createPasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getId())
                .expiryDate(Instant.now().plusSeconds(RESET_EXPIRY_HOURS * 3600))
                .build();

        passwordResetTokenRepository.deleteByUserId(user.getId());
        return passwordResetTokenRepository.save(resetToken);
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .orElse(null);
    }

    public void deletePasswordResetToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }

    public boolean isTokenExpired(Instant expiryDate) {
        return expiryDate.isBefore(Instant.now());
    }
}