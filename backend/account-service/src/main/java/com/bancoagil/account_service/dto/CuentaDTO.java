package com.bancoagil.account_service.dto;

import com.bancoagil.account_service.model.Cuenta;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class CuentaDTO {
    
    private Long id;
    private Long idCliente;
    private String numeroCuenta;
    private Cuenta.TipoCuenta tipoCuenta;
    private BigDecimal saldoActual;
    private Cuenta.EstadoCuenta estado;
    private ZonedDateTime fechaApertura;
}
