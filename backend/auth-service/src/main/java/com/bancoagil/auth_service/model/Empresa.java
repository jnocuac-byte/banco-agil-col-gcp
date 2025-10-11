package com.bancoagil.auth_service.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa a una empresa
@Entity
@Table(name="empresas") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class Empresa {

    // Identificador único de la empresa
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    // Id del cliente asociado a la empresa
    @Column(name="id_cliente", unique=true, nullable=false)
    private Long idCliente;

    // Relación uno a uno con la entidad Cliente
    @OneToOne
    @JoinColumn(name="id_cliente", insertable=false, updatable=false) // Mapea la columna id_cliente
    private Cliente cliente;

    // Información específica de la empresa
    @Column(name="nit", length=20, unique=true, nullable=false)
    private String nit;

    // Razón social de la empresa
    @Column(name="razon_social", length=200, nullable=false)
    private String razonSocial;

    // Nombre comercial de la empresa   
    @Column(name="nombre_comercial", length=200)
    private String nombreComercial;

    // Fecha de constitución de la empresa
    @Column(name="fecha_constitucion")
    private LocalDate fechaConstitucion;

    // Número de empleados
    @Column(name="num_empleados", length=50)
    private Integer numEmpleados;

    // Sector económico de la empresa
    @Column(name="sector_economico", length=100)
    private String sectorEconomico;

    // Estado de la empresa (activo/inactivo)
    @PrePersist
    protected void onCreate() {
        if (fechaConstitucion == null) {
            fechaConstitucion = LocalDate.now();
        }
    }

}
