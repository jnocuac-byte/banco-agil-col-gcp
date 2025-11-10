package com.bancoagil.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Clase principal de la aplicación Spring Boot
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AuthServiceApplication {

	// Método principal para iniciar la aplicación
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
