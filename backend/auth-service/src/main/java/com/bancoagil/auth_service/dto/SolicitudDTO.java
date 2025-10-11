package com.bancoagil.auth_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

// DTO para representar la información de una solicitud de crédito
@Data
public class SolicitudDTO {
    private Long id;
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
    private BigDecimal tasaInteres;
    private String estado;
    private LocalDateTime fechaSolicitud;
}