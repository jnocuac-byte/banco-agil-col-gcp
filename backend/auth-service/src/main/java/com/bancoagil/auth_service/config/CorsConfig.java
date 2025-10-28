package com.bancoagil.auth_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configuración de CORS para permitir solicitudes desde el frontend
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    // Permitir solicitudes CORS desde orígenes específicos
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**") // Rutas que permiten CORS
                .allowedOrigins("http://localhost:5500", "https://127.0.0.1:5500", "http://localhost:3000", "https://frontend-514751056677.us-central1.run.app") // Orígenes permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permitir todos los encabezados
                .allowCredentials(true); // Permitir credenciales (cookies, autenticación)
    }
}
