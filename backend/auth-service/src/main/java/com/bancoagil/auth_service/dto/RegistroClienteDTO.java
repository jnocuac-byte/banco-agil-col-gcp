package com.bancoagil.auth_service.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO para manejar el registro de un cliente
@Data
public class RegistroClienteDTO {
    // Email del cliente
    @NotBlank(message="El email es obligatorio")
    @Email(message="¡Email Inválido!") // Validación de formato de email
    private String email;

    // Contraseña del cliente
    @NotBlank(message="La contraseña es obligatoria")
    private String password;

    // Tipo de cliente
    @NotBlank(message="El tipo de cliente es obligatorio")
    private String tipoCliente;

    // Información común
    @NotBlank(message="El teléfono es obligatorio")
    private String telefono;

    // Dirección del cliente
    @NotBlank(message="La direccion es obligatoria")
    private String direccion;

    // Ciudad del cliente
    @NotBlank(message="La ciudad es obligatoria")
    private String ciudad;

    // Información específica para clientes personales
    private String numDocumento;
    private String tipoDocumento;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;

    // Información específica para clientes empresariales
    private String nit;
    private String razonSocial;
    private String nombreComercial;
    private LocalDate fechaConstitucion;
    private Integer numEmpleados;
    private String sectorEconomico;

}
