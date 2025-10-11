package com.bancoagil.credit_service.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa un crédito otorgado a un cliente
@Entity
@Table(name = "creditos_otorgados") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class CreditoOtorgado {
    
    // Identificador único del crédito otorgado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;
    
    // ID de la solicitud de crédito asociada
    @Column(name = "id_solicitud", unique = true, nullable = false)
    private Long idSolicitud;
    
    // ID del cliente que recibió el crédito
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;
    
    // ID de la cuenta donde se desembolsó el crédito
    @Column(name = "id_cuenta_desembolso", nullable = false)
    private Long idCuentaDesembolso;
    
    // Información fija del préstamo
    @Column(name = "monto_original", precision = 15, scale = 2, nullable = false)
    private BigDecimal montoOriginal;

    // Plazo del préstamo en meses
    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;
    
    // Tasa de interés anual
    @Column(name = "tasa_interes", precision = 5, scale = 2, nullable = false)
    private BigDecimal tasaInteres;
    
    // Cuota mensual fija
    @Column(name = "cuota_mensual", precision = 15, scale = 2, nullable = false)
    private BigDecimal cuotaMensual;
    
    // Saldos y estado (variables)
    @Column(name = "saldo_pendiente", precision = 15, scale = 2, nullable = false)
    private BigDecimal saldoPendiente;
    
    // Número de cuotas pagadas
    @Column(name = "cuotas_pagadas", nullable = false)
    private Integer cuotasPagadas = 0;
    
    // Número de cuotas pendientes
    @Column(name = "cuotas_pendientes", nullable = false)
    private Integer cuotasPendientes;
    
    // Fecha de la próxima cuota a pagar
    @Column(name = "proxima_fecha_pago")
    private LocalDate proximaFechaPago;
    
    // Estado del crédito
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_credito", length = 20, nullable = false)
    private EstadoCredito estadoCredito;
    
    // Fechas importantes
    @Column(name = "fecha_desembolso", nullable = false)
    private LocalDateTime fechaDesembolso;
    
    // Fecha de vencimiento final del crédito
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    // Método para inicializar valores antes de persistir
    @PrePersist
    protected void onCreate() {
        // Inicializar fechas y estado si no están establecidos
        if (fechaDesembolso == null) {
            fechaDesembolso = LocalDateTime.now();
        }
        // Calcular fecha de vencimiento
        if (estadoCredito == null) {
            estadoCredito = EstadoCredito.ACTIVO; // Estado inicial
        }
    }
    
    // Enumeración para los estados del crédito
    public enum EstadoCredito {
        ACTIVO,
        PAGADO,
        EN_MORA,
        CASTIGADO
    }
}