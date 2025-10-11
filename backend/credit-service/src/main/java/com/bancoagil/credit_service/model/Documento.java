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

// Entidad para representar documentos asociados a una solicitud de crédito
@Entity
@Table(name="documentos") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class Documento {
    
    // ID del documento
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // Auto-incremental
    private Long id;

    // ID de la solicitud de crédito asociada
    @Column(name="id_solicitud", nullable=false)
    private Long idSolicitud;

    // Relación muchos a uno con la entidad SolicitudCredito
    @ManyToOne
    @JoinColumn(name="id_solicitud", insertable=false, updatable=false) // Evitar actualizaciones directas
    private SolicitudCredito solicitud;

    // Tipo de documento (e.g., Identificación, Comprobante de Ingresos)
    @Column(name="tipo_documento", length=50, nullable=false)
    private String tipoDocumento;

    // Nombre original del archivo
    @Column(name="nombre_archivo", length=255, nullable=false)
    private String nombreArchivo;

    // Ruta donde se almacena el archivo en el sistema
    @Column(name="ruta_archivo", length=500, nullable=false)
    private String rutaArchivo;

    // Tamaño del archivo en bytes
    @Column(name="tamano_bytes")
    private Long tamanoBytes;

    // Fecha y hora en que se cargó el documento
    @Column(name="fecha_carga")
    private LocalDateTime fechaCarga;

    // Método para establecer la fecha de carga antes de persistir el registro
    @PrePersist
    protected void onCreate(){
        // Establecer la fecha actual si no está ya establecida
        if(fechaCarga == null){
            fechaCarga = LocalDateTime.now();
        }
    }

}
