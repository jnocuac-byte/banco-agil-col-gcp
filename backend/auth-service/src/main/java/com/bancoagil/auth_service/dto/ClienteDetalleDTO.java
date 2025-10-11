package com.bancoagil.auth_service.dto;

import java.time.LocalDateTime;

import lombok.Data;

// DTO para representar los detalles completos de un cliente
@Data
public class ClienteDetalleDTO {
    private Long clienteId;
    private String nombreCompleto;
    private String documento;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String tipoCliente;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}