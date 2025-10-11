package com.bancoagil.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Configuración de seguridad
@Configuration
public class SecurityConfig {
    
    // Bean para el codificador de contraseñas utilizando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Crear y devolver una instancia de BCryptPasswordEncoder
    }
}