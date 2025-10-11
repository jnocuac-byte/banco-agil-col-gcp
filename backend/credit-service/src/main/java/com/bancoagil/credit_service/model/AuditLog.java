package com.bancoagil.credit_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad para registrar logs de auditoría
@Entity
@Table(name="audit_logs") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class AuditLog {
    
    // ID del log de auditoría
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // Auto-incremental
    private Long id;

    // ID del usuario que realizó la acción
    @Column(name="id_usuario")
    private Long idUsuario;

    // Acción realizada (e.g., CREAR, ACTUALIZAR, ELIMINAR)
    @Column(name="accion", length=100, nullable=false)
    private String accion;

    // Entidad afectada (e.g., SolicitudCredito, Cliente)
    @Column(name="entidad", length=50, nullable=false)
    private String entidad;

    // ID de la entidad afectada
    @Column(name="id_entidad")
    private Long idEntidad;

    // Descripción detallada de la acción
    @Column(name="descripcion", columnDefinition="TEXT")
    private String descripcion;

    // Dirección IP desde donde se realizó la acción
    @Column(name="ip_address", length=45)
    private String ipAddress;

    // Fecha y hora de la acción
    @Column(name="fecha", nullable=false)
    private LocalDateTime fecha;

    // Método para establecer la fecha antes de persistir el registro
    @PrePersist
    protected void onCreate(){
        // Establecer la fecha actual si no está ya establecida
        if(fecha == null){
            fecha = LocalDateTime.now();
        }
    }

}
