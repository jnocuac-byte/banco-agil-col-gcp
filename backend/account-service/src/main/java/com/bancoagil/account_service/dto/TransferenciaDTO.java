package com.bancoagil.account_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferenciaDTO {
    
    @NotNull(message = "El número de cuenta origen es obligatorio")
    @Size(min = 10, max = 20, message = "El número de cuenta origen debe tener entre 10 y 20 caracteres")
    private String numeroCuentaOrigen;
    
    @NotNull(message = "El número de cuenta destino es obligatorio")
    @Size(min = 10, max = 20, message = "El número de cuenta destino debe tener entre 10 y 20 caracteres")
    private String numeroCuentaDestino;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor que cero")
    private BigDecimal monto;
    
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
}
