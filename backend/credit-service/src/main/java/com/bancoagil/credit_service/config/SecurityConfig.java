package com.bancoagil.credit_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Configuración de seguridad para el servicio de créditos
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configuración del filtro de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF (común en APIs)
            .authorizeHttpRequests(auth -> auth
                // Permite acceso sin autenticar (SOLO PARA PRUEBAS INICIALES)
                .requestMatchers("/api/solicitudes/**", "/api/documentos/**") // Rutas permitidas sin autenticación
                .permitAll() // Permitir acceso sin autenticación
                .anyRequest().authenticated() // Requiere autenticación para otras rutas
            );
        return http.build(); // Construir y devolver el filtro de seguridad
    }
    
    // Bean para el codificador de contraseñas utilizando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Crear y devolver una instancia de BCryptPasswordEncoder
    }
}