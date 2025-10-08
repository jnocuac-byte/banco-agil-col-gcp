package com.bancoagil.auth_service.Dtos;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmpresaResponseDTO {

    private Integer id; // La PK de la tabla empresas
    private Integer idCliente; // La FK a la tabla clientes
    private Integer idUsuario; // Obtenido desde Cliente.usuario.id
    private String emailUsuario; // Obtenido desde Cliente.usuario.email
    
    private String razonSocial;
    private String ruc;
    private String nombreRepresentante;
}
