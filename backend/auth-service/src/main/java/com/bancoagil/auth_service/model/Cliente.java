package com.bancoagil.auth_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa a un cliente
@Entity
@Table(name="clientes") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class Cliente {
    
    // Identificador único del cliente
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // Generación automática del ID
    private long id;

    // Nombres del cliente
    @Column(name="id_usuario", unique=true, nullable=false) // ID del usuario asociado al cliente
    private long idUsuario;

    // Relación uno a uno con la entidad Usuario
    @OneToOne
    @JoinColumn(name="id_usuario", insertable=false, updatable=false) // Mapea la columna id_usuario
    private Usuario usuario;

    // Nombres del cliente
    @Column(name="tipo_cliente")
    @Enumerated(EnumType.STRING) // Almacena el enum como cadena en la base de datos
    private TipoCliente tipoCliente;

    // Nombres del cliente
    @Column(name="telefono", length=20)
    private String telefono;

    // Documento de identidad del cliente
    @Column(name="direccion", length=200)
    private String direccion;

    // Documento de identidad del cliente
    @Column(name="ciudad", length=100)
    private String ciudad;

    // Estado del cliente (activo/inactivo)
    public enum TipoCliente{
        PERSONA_NATURAL,
        EMPRESA
    }

}
