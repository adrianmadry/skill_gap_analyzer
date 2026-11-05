package com.skillgap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.skillgap.dao.UserRepository;
import com.skillgap.entity.AppUser;
import com.skillgap.entity.enums.UserRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@RequiredArgsConstructor 
@Slf4j 
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional 
    public void createAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin account already exists, skipping creation");
            return;
        }

        userRepository.save(new AppUser(adminEmail, passwordEncoder.encode(adminPassword), UserRole.ROLE_ADMIN));
        log.info("Default admin account created: {}", adminEmail);
    
    }

}
