package com.bancoagil.credit_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudCreditoDTO {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;
    
    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1000000, message = "El monto mínimo es $1.000.000")
    private BigDecimal montoSolicitado;
    
    @NotNull(message = "El plazo es obligatorio")
    @Min(value = 6, message = "El plazo mínimo es 6 meses")
    private Integer plazoMeses;
    
    private String observaciones;
}