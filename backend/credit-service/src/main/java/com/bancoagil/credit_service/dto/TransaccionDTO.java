package com.bancoagil.credit_service.dto;

import java.math.BigDecimal;

import lombok.Data;

// DTO para representar la información de una transacción
@Data
public class TransaccionDTO {
    private BigDecimal monto; // Monto de la transacción
    private String descripcion; // Descripción de la transacción
}