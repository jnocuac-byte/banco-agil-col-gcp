package com.bancoagil.account_service.dto;

import com.bancoagil.account_service.model.PersonaNatural;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonaNaturalCreateDTO {
    // ID del usuario creado en auth-service
    private Integer idUsuario;
    
    // Datos del cliente
    private String telefono;
    private String direccion;
    private String ciudad;
    
    // Datos específicos de persona natural
    private String numDocumento;
    private PersonaNatural.TipoDocumento tipoDocumento;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
}
