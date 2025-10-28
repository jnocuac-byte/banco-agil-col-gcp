package com.bancoagil.credit_service.config;

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
                .allowedOrigins("https://frontend-service-514751056677.us-central1.run.app") // Orígenes permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permitir todos los encabezados
                .allowCredentials(true); // Permitir credenciales (cookies, autenticación)
    }
}
