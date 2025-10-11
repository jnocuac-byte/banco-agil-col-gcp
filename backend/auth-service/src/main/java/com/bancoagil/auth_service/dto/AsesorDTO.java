package com.bancoagil.auth_service.dto;

import lombok.Data;

// DTO para representar la información de un asesor
@Data
public class AsesorDTO {
    private Long asesorId;
    private String nombreCompleto;
    private String codigoEmpleado;
    private String email;
    private String area;
    private Boolean activo;
}