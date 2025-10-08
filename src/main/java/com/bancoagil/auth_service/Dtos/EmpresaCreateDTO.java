package com.bancoagil.auth_service.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmpresaCreateDTO {

    // Clave Foránea: ID del Cliente asociado.
    @NotNull(message = "El ID del cliente asociado es obligatorio.")
    private Integer idCliente; 

    @NotNull(message = "La razón social es obligatoria.")
    @Size(max = 150)
    private String razonSocial;

    @NotNull(message = "El RUC/NIT es obligatorio.")
    @Size(max = 50)
    private String ruc; // Debe coincidir con el nombre de la propiedad en la entidad

    @NotNull(message = "El nombre del representante legal es obligatorio.")
    @Size(max = 100)
    private String nombreRepresentante;
}