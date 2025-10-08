package com.bancoagil.auth_service.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar recursos duplicados (código HTTP 409).
 */
@ResponseStatus(HttpStatus.CONFLICT) // Indica que debe retornar un 409
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
