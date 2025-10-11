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

// Entidad para representar un pago realizado hacia un crédito
@Entity
@Table(name = "pagos_credito") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class PagoCredito {
    
    // ID del pago
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremental
    private Long id;
    
    // ID del crédito asociado
    @Column(name = "id_credito", nullable = false)
    private Long idCredito;
    
    // Número de cuota pagada
    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;
    
    // Monto total del pago
    @Column(name = "monto_pago", precision = 15, scale = 2, nullable = false)
    private BigDecimal montoPago;
    
    // Desglose del pago
    @Column(name = "monto_capital", precision = 15, scale = 2, nullable = false)
    private BigDecimal montoCapital;
    
    // Monto correspondiente a intereses
    @Column(name = "monto_interes", precision = 15, scale = 2, nullable = false)
    private BigDecimal montoInteres;
    
    // Monto correspondiente a penalizaciones por pagos tardíos
    @Column(name = "saldo_restante", precision = 15, scale = 2, nullable = false)
    private BigDecimal saldoRestante;
    
    // Fecha del pago
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
    
    // Fecha programada para el pago
    @Column(name = "fecha_programada")
    private LocalDate fechaProgramada;
    
    // Estado del pago (COMPLETADO, TARDIO, ADELANTADO)
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", length = 20, nullable = false) // Estado del pago
    private EstadoPago estadoPago;
    
    // Método para inicializar valores antes de persistir
    @PrePersist
    protected void onCreate() {
        // Inicializar fecha de pago y estado si no están establecidos
        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }
        // Inicializar estado del pago si no está establecido
        if (estadoPago == null) {
            estadoPago = EstadoPago.COMPLETADO; // Estado inicial
        }
    }
    
    // Enumeración para los estados del pago
    public enum EstadoPago {
        COMPLETADO,
        TARDIO,
        ADELANTADO
    }
}