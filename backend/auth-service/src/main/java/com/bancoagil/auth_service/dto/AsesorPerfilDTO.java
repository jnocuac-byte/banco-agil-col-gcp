package com.bancoagil.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para representar el perfil de un asesor
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsesorPerfilDTO {
    private Long asesorId;
    private String email;
    private String nombres;
    private String apellidos;
    private String codigoEmpleado;
    private String area;
    private String nombreCompleto; // nombres + " " + apellidos
}