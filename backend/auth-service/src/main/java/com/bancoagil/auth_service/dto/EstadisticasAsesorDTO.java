package com.bancoagil.auth_service.dto;

import lombok.Data;

// DTO para representar estadísticas de un asesor
@Data
public class EstadisticasAsesorDTO {
    private Long totalProcesadas;
    private Long aprobadas;
}