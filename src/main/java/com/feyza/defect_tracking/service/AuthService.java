package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.annotation.LogExecution;
import com.feyza.defect_tracking.dto.auth.AuthRequest;
import com.feyza.defect_tracking.dto.auth.AuthResponse;
import com.feyza.defect_tracking.dto.auth.RegisterRequest;
import com.feyza.defect_tracking.entity.User;
import com.feyza.defect_tracking.exception.BusinessException;
import com.feyza.defect_tracking.repository.UserRepository;
import com.feyza.defect_tracking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @LogExecution(action = "LOGIN", entityType = "USER")
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }

    @LogExecution(action = "REGISTER", entityType = "USER")
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("Username is already taken!");
        }

        User user = new User();
        user.setUsername(request.getUsername());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(request.getRole());

        userRepository.save(user);
    }
}
