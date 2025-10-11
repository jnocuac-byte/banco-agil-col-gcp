package com.bancoagil.auth_service.dto;

import lombok.Data;

// DTO para representar la información de un cliente
@Data
public class ClienteDTO {
    private Long clienteId;
    private String nombreCompleto;
    private String documento;
    private String email;
    private String telefono;
    private String ciudad;
    private String tipoCliente;
    private Boolean activo;
}