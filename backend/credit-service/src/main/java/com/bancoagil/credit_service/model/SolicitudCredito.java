package com.bancoagil.credit_service.model;

import java.math.BigDecimal;
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

// Entidad para representar una solicitud de crédito
@Entity
@Table(name="solicitudes_credito") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class SolicitudCredito {
    
    // ID de la solicitud de crédito
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // Auto-incremental
    private Long id;

    // ID del cliente que realiza la solicitud
    @Column(name="id_cliente", nullable=false)
    private Long idCliente;

    // Monto solicitado para el crédito
    @Column(name="monto_solicitado", nullable=false, precision=15, scale=2)
    private BigDecimal montoSolicitado;

    // Plazo en meses para el crédito
    @Column(name="plazo_meses", nullable=false)
    private Integer plazoMeses;

    // Tasa de interés aplicada al crédito
    @Column(name="tasa_interes", precision=5, scale=2)
    private BigDecimal tasaInteres;

    // Estado de la solicitud (e.g., PENDIENTE, APROBADA, RECHAZADA)
    @Column(name="estado", length=30, nullable=false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    // ID del asesor asignado a la solicitud
    @Column(name="id_asesor_asignado")
    private Long idAsesorAsignado;

    // Fecha y hora en que se realizó la solicitud
    @Column(name="fecha_solicitud", nullable=false)
    private LocalDateTime fechaSolicitud;

    // Fecha y hora de la última actualización de la solicitud
    @Column(name="fecha_decision", nullable=false)
    private LocalDateTime fechaDecision;

    // Observaciones o comentarios adicionales sobre la solicitud
    @Column(name="observaciones", columnDefinition="TEXT")
    private String observaciones;

    // Método para establecer valores por defecto antes de persistir el registro
    @PrePersist
    protected void onCreate(){
        // Establecer la fecha de solicitud actual si no está ya establecida
        if(fechaSolicitud == null){
            fechaSolicitud = LocalDateTime.now();
        }
        // Establecer el estado inicial como PENDIENTE si no está ya establecido
        if(estado == null){
            estado = Estado.PENDIENTE;
        }
    }

    // Enum para los posibles estados de la solicitud de crédito
    public enum Estado{
        PENDIENTE,
        EN_REVISION,
        APROBADA,
        RECHAZADA
    }

}
