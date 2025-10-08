package com.bancoagil.account_service.dto;

import com.bancoagil.account_service.model.Transaccion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class TransaccionDTO {
    
    private Long id;
    private Long idCuentaOrigen;
    private Long idCuentaDestino;
    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
    private Transaccion.TipoTransaccion tipoTransaccion;
    private BigDecimal monto;
    private String descripcion;
    private ZonedDateTime fechaTransaccion;
    private Transaccion.EstadoTransaccion estado;
}
