package com.bancoagil.credit_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// Configuración de RestTemplate para llamadas HTTP
@Configuration
public class RestTemplateConfig {
    
    // Bean para RestTemplate
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Crear y devolver una instancia de RestTemplate
    }
}