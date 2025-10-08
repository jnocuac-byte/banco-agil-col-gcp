package com.bancoagil.auth_service.Dtos;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmpresaUpdateDTO {

    @Size(max = 150)
    private String razonSocial;

    // Asumimos que el RUC/NIT no se puede actualizar o que es raro. Lo dejamos sin @NotNull.
    @Size(max = 50)
    private String ruc; 

    @Size(max = 100)
    private String nombreRepresentante;
}