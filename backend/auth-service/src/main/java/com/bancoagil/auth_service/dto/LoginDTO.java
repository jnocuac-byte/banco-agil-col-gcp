package com.bancoagil.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message="El email es obligatorio")
    @Email(message="Email Inválido")
    private String email;

    @NotBlank(message="La contraseña es obligatoria")
    private String password;

}