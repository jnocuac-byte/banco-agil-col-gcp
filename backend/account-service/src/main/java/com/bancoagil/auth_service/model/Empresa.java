package com.bancoagil.auth_service.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="empresas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="id_cliente", unique=true, nullable=false)
    private Long idCliente;

    @OneToOne
    @JoinColumn(name="id_cliente", insertable=false, updatable=false)
    private Cliente cliente;

    @Column(name="nit", length=20, unique=true, nullable=false)
    private String nit;

    @Column(name="razon_social", length=200, nullable=false)
    private String razonSocial;

    @Column(name="nombre_comercial", length=200)
    private String nombreComercial;

    @Column(name="fecha_constitucion")
    private LocalDate fechaConstitucion;

    @Column(name="num_empleados", length=50)
    private String numEmpleados;

    @Column(name="sector_economico", length=100)
    private String sectorEconomico;

    /*
    @PrePersist
    protected void onCreate() {
        if (fechaConstitucion == null) {
            fechaConstitucion = LocalDate.now();
        }
    }*/

}
