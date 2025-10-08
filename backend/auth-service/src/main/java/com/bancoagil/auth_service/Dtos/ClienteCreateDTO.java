package com.bancoagil.auth_service.Dtos;

import com.bancoagil.auth_service.Enums.TipoCliente;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClienteCreateDTO {

    @NotNull(message = "El ID de usuario asociado es obligatorio.")
    private Integer idUsuario; // <--- MANTENER ESTE CAMPO

    @NotNull(message = "El tipo de cliente es obligatorio (NATURAL/EMPRESA).")
    private TipoCliente tipoCliente;

    @Size(max = 20)
    private String telefono;

    @Size(max = 200)
    private String direccion;

    @Size(max = 100)
    private String ciudad;
}