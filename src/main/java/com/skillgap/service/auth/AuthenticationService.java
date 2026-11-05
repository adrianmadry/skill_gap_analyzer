package com.skillgap.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillgap.dao.UserRepository;
import com.skillgap.dto.auth.AuthResponseDto;
import com.skillgap.dto.auth.LoginRequestDto;
import com.skillgap.dto.auth.RegisterRequestDto;
import com.skillgap.entity.AppUser;
import com.skillgap.entity.enums.UserRole;
import com.skillgap.exception.auth.PasswordsDoNotMatchException;
import com.skillgap.exception.auth.UserAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Transactional 
    public AuthResponseDto register(RegisterRequestDto request) {

        String email = request.email().trim().toLowerCase();

        if (!request.password().equals(request.confirmPassword())) {
            log.warn("Registration failed: Passwords do not match for email: {}", email);
            throw new PasswordsDoNotMatchException();
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: Email already exists: {}", email);
            throw new UserAlreadyExistsException(email);
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        AppUser user = new AppUser(email, hashedPassword, UserRole.ROLE_USER);

        userRepository.save(user);
        log.info("New user registered: {}", email);

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken);

    }

    public AuthResponseDto login(LoginRequestDto request) {

        String email = request.email().trim().toLowerCase();

        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, request.password())
        );

        AppUser user = userRepository.findByEmail(email)
                        .orElseThrow();
        log.info("User logged in: {}", email);

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken);

    }

}
