package com.bancoagil.auth_service.Dtos;

import java.sql.Date;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonaNaturalResponseDTO {

    private Integer id; // La PK de la tabla persona_natural
    private Integer idCliente; // La FK a la tabla clientes
    private Integer idUsuario; // Para tener una trazabilidad completa (obtenido desde Cliente.usuario.id)
    private String emailUsuario; // Para tener una trazabilidad completa (obtenido desde Cliente.usuario.email)
    
    private String nombre;
    private String apellido;
    private String identificacion;
    private Date fechaNacimiento;
}