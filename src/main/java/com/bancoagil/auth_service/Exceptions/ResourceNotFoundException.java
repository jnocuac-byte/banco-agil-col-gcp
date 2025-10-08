package com.bancoagil.auth_service.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar recursos no encontrados (código HTTP 404).
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Indica que debe retornar un 404
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
