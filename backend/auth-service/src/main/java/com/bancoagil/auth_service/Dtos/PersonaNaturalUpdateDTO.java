package com.bancoagil.auth_service.Dtos;

import java.sql.Date;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonaNaturalUpdateDTO {

    // Nota: El idCliente y la identificación generalmente NO se actualizan.
    // Solo incluimos campos que son susceptibles de cambio (e.g., si se permite corrección de fecha).

    @Size(max = 100)
    private String nombre;

    @Size(max = 100)
    private String apellido;
    
    // Asumimos que la identificación no se puede actualizar fácilmente, pero la incluimos
    // si el negocio lo permite (sin @NotNull para que sea opcional en el PUT).
    @Size(max = 50)
    private String identificacion; 

    private Date fechaNacimiento;
}