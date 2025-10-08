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

@Entity
@Table(name="audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="id_usuario")
    private Long idUsuario;

    @Column(name="accion", length=100, nullable=false)
    private String accion;

    @Column(name="entidad", length=50, nullable=false)
    private String entidad;

    @Column(name="id_entidad")
    private Long idEntidad;

    @Column(name="descripcion", columnDefinition="TEXT")
    private String descripcion;

    @Column(name="ip_address", length=45)
    private String ipAddress;

    @Column(name="fecha", nullable=false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate(){
        if(fecha == null){
            fecha = LocalDateTime.now();
        }
    }

}
