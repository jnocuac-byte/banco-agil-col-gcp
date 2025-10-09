package com.bancoagil.credit_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO para la solicitud de crédito
@Data
public class SolicitudCreditoDTO {
    
    // ID del cliente que realiza la solicitud
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;
    
    // Monto solicitado para el crédito
    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1000000, message = "El monto mínimo es $1.000.000") // Ejemplo de monto mínimo
    private BigDecimal montoSolicitado;
    
    // Plazo en meses para el crédito
    @NotNull(message = "El plazo es obligatorio")
    @Min(value = 6, message = "El plazo mínimo es 6 meses") // Ejemplo de plazo mínimo
    private Integer plazoMeses;
    
    // Motivo de la solicitud de crédito
    private String observaciones;
}