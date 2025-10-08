package com.bancoagil.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean success;
    private String message;
    private Long usuarioId;
    private String email;
    private String tipoUsuario;
    private String token; //JWT

    //Datos del cliente adicionales
    private Long clienteId;
    private String tipoCliente;
    private String nombreCompleto;
}
