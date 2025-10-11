package com.bancoagil.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// Configuración de la aplicación
@Configuration
public class AppConfig {
    
    // Bean para RestTemplate, utilizado para hacer llamadas HTTP a otros servicios
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Crear y devolver una instancia de RestTemplate
    }
}