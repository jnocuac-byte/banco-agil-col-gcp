package com.bancoagil.account_service.dto;

import com.bancoagil.account_service.model.Cliente;
import lombok.Data;

@Data
public class ClienteDTO {
    private Long id;
    private Integer idUsuario;
    private String telefono;
    private Cliente.TipoCliente tipoCliente;
    private String ciudad;
    private String direccion;
}
