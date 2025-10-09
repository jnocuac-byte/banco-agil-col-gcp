package com.bancoagil.auth_service.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistroClienteDTO {
    @NotBlank(message="El email es obligatorio")
    @Email(message="¡Email Inválido!")
    private String email;

    @NotBlank(message="La contraseña es obligatoria")
    private String password;

    @NotBlank(message="El tipo de cliente es obligatorio")
    private String tipoCliente;

    @NotBlank(message="El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message="La direccion es obligatoria")
    private String direccion;

    @NotBlank(message="La ciudad es obligatoria")
    private String ciudad;


    private String numDocumento;
    private String tipoDocumento;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;

    private String nit;
    private String razonSocial;
    private String nombreComercial;
    private LocalDate fechaConstitucion;
    private Integer numEmpleados;
    private String sectorEconomico;

}
