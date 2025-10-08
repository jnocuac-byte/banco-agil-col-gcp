package com.bancoagil.auth_service.Dtos;

import java.sql.Timestamp;

import com.bancoagil.auth_service.Enums.TipoUsuario;

import lombok.Builder;
import lombok.Value;

/**
 * DTO para la respuesta de un Usuario.
 * Contiene solo los campos que son seguros para devolver al cliente.
 */
@Value // Genera Getters y constructor inmutable
@Builder // Patrón Builder
public class UsuarioResponseDTO {

    // Identificador único del usuario.
    Integer id;

    // Email del usuario (nombre de usuario).
    String email;

    // Tipo de usuario (CLIENTE, ASESOR, etc.).
    TipoUsuario tipoUsuario;

    // Estado de la cuenta.
    Boolean activo;

    // Fecha en que fue creado el registro.
    Timestamp fechaCreacion;

    // La contraseña (password) de la Entity se omite intencionalmente por seguridad.
}