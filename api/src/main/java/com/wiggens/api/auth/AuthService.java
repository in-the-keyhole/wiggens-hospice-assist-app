package com.wiggens.api.auth;

import com.wiggens.api.auth.dto.*;
import com.wiggens.api.security.JwtService;
import com.wiggens.api.user.User;
import com.wiggens.api.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(Instant.now())
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
        ));
        String token = jwtService.generateToken(request.getEmail().toLowerCase());
        return new AuthResponse(token);
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(user -> {
                    tokenRepository.deleteByUserId(user.getId());
                    PasswordResetToken token = PasswordResetToken.builder()
                            .user(user)
                            .token(UUID.randomUUID().toString())
                            .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                            .build();
                    tokenRepository.save(token);
                    // In production, send email with token link. For dev/test, stored in DB.
                });
        // Always return 200 to avoid leaking which emails exist.
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (token.isExpired()) {
            throw new IllegalArgumentException("Expired token");
        }
        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenRepository.delete(token);
    }
}

