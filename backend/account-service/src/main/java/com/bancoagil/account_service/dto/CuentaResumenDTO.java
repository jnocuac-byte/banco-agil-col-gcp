package com.bancoagil.account_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CuentaResumenDTO {
    
    private CuentaDTO cuenta;
    private List<TransaccionDTO> ultimasTransacciones;
    
    // Información adicional del resumen
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private Integer cantidadTransacciones;
}
