package com.bancoagil.account_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DesembolsoDTO {
    
    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long idCuenta;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor que cero")
    private BigDecimal monto;
    
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
    
    @NotNull(message = "El ID de la solicitud es obligatorio")
    private Long idSolicitud;
}