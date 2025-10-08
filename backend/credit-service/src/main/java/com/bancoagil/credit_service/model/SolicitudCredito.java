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

@Entity
@Table(name="solicitudes_credito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudCredito {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="id_cliente", nullable=false)
    private Long idCliente;

    @Column(name="monto_solicitado", nullable=false, precision=15, scale=2)
    private BigDecimal montoSolicitado;

    @Column(name="plazo_meses", nullable=false)
    private Integer plazoMeses;

    @Column(name="tasa_interes", precision=5, scale=2)
    private BigDecimal tasaInteres;

    @Column(name="estado", length=30, nullable=false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(name="id_asesor_asignado")
    private Long idAsesorAsignado;

    @Column(name="fecha_solicitud", nullable=false)
    private LocalDateTime fechaSolicitud;

    @Column(name="fecha_decision", nullable=false)
    private LocalDateTime fechaDecision;

    @Column(name="observaciones", columnDefinition="TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate(){
        if(fechaSolicitud == null){
            fechaSolicitud = LocalDateTime.now();
        }
        if(estado == null){
            estado = Estado.PENDIENTE;
        }
    }

    public enum Estado{
        PENDIENTE,
        EN_REVISION,
        APROBADA,
        RECHAZADA
    }

}
