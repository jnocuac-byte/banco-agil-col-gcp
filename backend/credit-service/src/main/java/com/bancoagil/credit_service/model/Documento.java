package com.bancoagil.credit_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Documento {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="id_solicitud", nullable=false)
    private Long idSolicitud;

    @ManyToOne
    @JoinColumn(name="id_solicitud", insertable=false, updatable=false)
    private SolicitudCredito solicitud;

    @Column(name="tipo_documento", length=50, nullable=false)
    private String tipoDocumento;

    @Column(name="nombre_archivo", length=255, nullable=false)
    private String nombreArchivo;

    @Column(name="ruta_archivo", length=500, nullable=false)
    private String rutaArchivo;

    @Column(name="tamano_bytes")
    private Long tamanoBytes;

    @Column(name="fecha_carga")
    private LocalDateTime fechaCarga;

    @PrePersist
    protected void onCreate(){
        if(fechaCarga == null){
            fechaCarga = LocalDateTime.now();
        }
    }

}
