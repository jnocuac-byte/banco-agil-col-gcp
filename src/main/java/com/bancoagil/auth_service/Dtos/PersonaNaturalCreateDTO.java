package com.bancoagil.auth_service.Dtos;

import java.sql.Date;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonaNaturalCreateDTO {

    // Clave Foránea: Necesitamos saber a qué Cliente se asocia.
    @NotNull(message = "El ID del cliente asociado es obligatorio.")
    private Integer idCliente; 

    @NotNull(message = "El nombre es obligatorio.")
    @Size(max = 100)
    private String nombre;

    @NotNull(message = "El apellido es obligatorio.")
    @Size(max = 100)
    private String apellido;

    @NotNull(message = "La identificación es obligatoria.")
    @Size(max = 50)
    private String identificacion;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    private Date fechaNacimiento;
}