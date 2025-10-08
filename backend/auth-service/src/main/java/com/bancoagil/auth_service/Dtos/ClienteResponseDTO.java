package com.bancoagil.auth_service.Dtos;

import com.bancoagil.auth_service.Enums.TipoCliente;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClienteResponseDTO {

    private Integer idCliente; // <--- NUEVA PK GENERADA DE LA TABLA CLIENTES
    private Integer idUsuario; // <--- FK DEL USUARIO ASOCIADO
    private String emailUsuario; 
    private TipoCliente tipoCliente;
    private String telefono;
    private String direccion;
    private String ciudad;
}