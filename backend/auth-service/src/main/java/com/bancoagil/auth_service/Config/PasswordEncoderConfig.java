package com.bancoagil.auth_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración que define el PasswordEncoder como un Bean de Spring,
 * resolviendo el error de inyección en UsuarioServiceImpl.
 */
@Configuration
public class PasswordEncoderConfig {

    // Este método define el objeto PasswordEncoder y lo pone a disposición 
    // del contexto de Spring para que pueda ser inyectado automáticamente.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }
}