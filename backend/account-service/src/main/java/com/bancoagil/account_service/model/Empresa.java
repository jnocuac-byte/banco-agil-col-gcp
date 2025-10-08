package com.bancoagil.account_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "empresas")
@Getter
@Setter
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", unique = true, nullable = false)
    private Long idCliente;

    @Column(unique = true, nullable = false, length = 20)
    private String nit;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "fecha_constitucion")
    private LocalDate fechaConstitucion;

    @Column(name = "num_empleados")
    private Integer numEmpleados;

    @Column(name = "sector_economico", length = 100)
    private String sectorEconomico;

    @OneToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id", insertable = false, updatable = false)
    private Cliente cliente;
}
