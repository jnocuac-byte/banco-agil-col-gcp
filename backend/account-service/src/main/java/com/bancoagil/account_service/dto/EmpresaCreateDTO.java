package com.bancoagil.account_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpresaCreateDTO {
    // ID del usuario creado en auth-service
    private Integer idUsuario;
    
    // Datos del cliente
    private String telefono;
    private String direccion;
    private String ciudad;
    
    // Datos específicos de empresa
    private String nit;
    private String razonSocial;
    private String nombreComercial;
    private LocalDate fechaConstitucion;
    private Integer numEmpleados;
    private String sectorEconomico;
}
