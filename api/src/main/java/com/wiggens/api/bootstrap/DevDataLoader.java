package com.wiggens.api.bootstrap;

import com.wiggens.api.patient.PatientProfile;
import com.wiggens.api.patient.PatientProfileRepository;
import com.wiggens.api.user.User;
import com.wiggens.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DevDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PatientProfileRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String email = "demo@example.com";
        String password = "Password123!";
        if (userRepository.existsByEmailIgnoreCase(email)) return;
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(Instant.now())
                .build();
        userRepository.save(user);
        patientRepository.save(PatientProfile.builder()
                .user(user)
                .fullName("Demo Patient")
                .contactPhone("555-0100")
                .hospiceOrganization("Wiggens Hospice")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());
    }
}
