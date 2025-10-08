package com.bancoagil.account_service.dto;

import com.bancoagil.account_service.model.Cuenta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CuentaCreateDTO {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;
    
    @NotNull(message = "El número de cuenta es obligatorio")
    @Size(min = 10, max = 20, message = "El número de cuenta debe tener entre 10 y 20 caracteres")
    private String numeroCuenta;
    
    @NotNull(message = "El tipo de cuenta es obligatorio")
    private Cuenta.TipoCuenta tipoCuenta;
    
    private BigDecimal saldoInicial = BigDecimal.ZERO;
}
