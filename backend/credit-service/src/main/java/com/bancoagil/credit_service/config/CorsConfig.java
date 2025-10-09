package com.bancoagil.credit_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configuración CORS para permitir solicitudes desde el frontend
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    // Configurar CORS
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Permitir solicitudes desde los orígenes del frontend
        registry.addMapping("/api/**") // Ajusta el patrón según tus necesidades
                .allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000") // Orígenes permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permitir todos los encabezados
                .allowCredentials(true); // Permitir credenciales (cookies, autenticación)
    }
}