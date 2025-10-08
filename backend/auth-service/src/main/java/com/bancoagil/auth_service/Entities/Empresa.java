package com.bancoagil.auth_service.Entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // Importamos ToString
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad que mapea la tabla 'empresas'.
 * Contiene información específica de clientes que son entidades jurídicas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresas")
@ToString(exclude = "cliente") // Excluimos la relación para evitar bucles de Lombok
public class Empresa {

    // Clave Primaria Autoincremental (PK de Empresa)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación One-to-One: Una empresa tiene un cliente asociado.
    // Esta entidad es la dueña de la FK (la columna 'id_cliente')
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", unique = true, nullable = false)
    private Cliente cliente; // Clave Foránea a la tabla 'clientes'

    // Columna: razon_social (varchar(150) NN)
    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    // Columna: ruc (varchar(50) NN)
    @Column(name = "ruc", nullable = false, length = 50, unique = true)
    private String ruc;

    // Columna: nombre_representante (varchar(100) NN)
    @Column(name = "nombre_representante", nullable = false, length = 100)
    private String nombreRepresentante;
}