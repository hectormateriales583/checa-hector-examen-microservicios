package com.tecsup.biblioteca.authservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody(required = false) Map<String, String> credentials) {
        return Map.of(
                "token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc4NDY4MTM2MX0.simulatedToken12345",
                "tipo", "Bearer",
                "usuario", "admin"
        );
    }
}
