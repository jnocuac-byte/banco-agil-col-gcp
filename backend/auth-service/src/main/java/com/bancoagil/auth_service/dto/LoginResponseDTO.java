package com.bancoagil.auth_service.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para manejar la respuesta del login
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
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String ciudad;
    private String direccion;
    private String documentoIdentidadEstado;
}
