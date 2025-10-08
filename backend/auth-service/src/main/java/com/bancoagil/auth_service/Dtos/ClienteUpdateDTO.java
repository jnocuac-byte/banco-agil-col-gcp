package com.bancoagil.auth_service.Dtos;

import com.bancoagil.auth_service.Enums.TipoCliente;

import lombok.AllArgsConstructor; // Usamos @Data para flexibilidad en DTOs de update
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteUpdateDTO {
    
    // Todos son opcionales en el DTO de update
    private TipoCliente tipoCliente;
    private String telefono;
    private String direccion;
    private String ciudad;
}