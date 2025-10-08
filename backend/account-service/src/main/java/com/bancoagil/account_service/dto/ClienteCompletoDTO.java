package com.bancoagil.account_service.dto;

import com.bancoagil.account_service.model.Cliente;
import com.bancoagil.account_service.model.PersonaNatural;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClienteCompletoDTO {
    // Datos del cliente
    private Long id;
    private Integer idUsuario;
    private Cliente.TipoCliente tipoCliente;
    private String telefono;
    private String direccion;
    private String ciudad;
    
    // Datos del usuario
    private String email;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    
    // Datos específicos según el tipo
    // Para persona natural
    private String numDocumento;
    private PersonaNatural.TipoDocumento tipoDocumento;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    
    // Para empresa
    private String nit;
    private String razonSocial;
    private String nombreComercial;
    private LocalDate fechaConstitucion;
    private Integer numEmpleados;
    private String sectorEconomico;
}
