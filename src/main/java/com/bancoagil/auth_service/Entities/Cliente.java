package com.bancoagil.auth_service.Entities;

import com.bancoagil.auth_service.Enums.TipoCliente;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad que mapea la tabla 'clientes'.
 * Representa la información de un usuario cuando este es de tipo 'CLIENTE'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 👈 El ID del cliente es independiente
    private Integer id;

    // Relación One-to-One: Un cliente tiene un usuario asociado.
    // Usamos CascadeType.ALL para asegurar que Hibernate maneje la entidad Usuario
    // dentro de la sesión al guardar Cliente, evitando el StaleObjectStateException.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", unique = true, nullable = false)
    private Usuario usuario;

    // Columna: tipo_cliente (varchar(20))
    @Enumerated(EnumType.STRING) // Guarda el nombre del enum (NATURAL, EMPRESA) como String
    @Column(name = "tipo_cliente", length = 20)
    private TipoCliente tipoCliente;

    // Columna: telefono (varchar(20))
    @Column(name = "telefono", length = 20)
    private String telefono;

    // Columna: direccion (varchar(200))
    @Column(name = "direccion", length = 200)
    private String direccion;

    // Columna: ciudad (varchar(100))
    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
    @ToString.Exclude
    private PersonaNatural personaNatural; 
    
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
    @ToString.Exclude
    private Empresa empresa;
}