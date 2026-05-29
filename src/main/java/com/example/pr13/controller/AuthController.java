package com.example.pr13.controller;

import com.example.pr13.config.JwtService;
import com.example.pr13.entity.User;
import com.example.pr13.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("ROLE_USER");
        userRepository.save(user);
        return "Користувача успішно зареєстровано";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        User dbUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        if (passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            String token = jwtService.generateToken(dbUser.getEmail());
            return Map.of("token", token);
        }
        throw new RuntimeException("Невірні дані для входу");
    }
}