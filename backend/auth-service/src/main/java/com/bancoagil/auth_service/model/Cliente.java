package com.bancoagil.auth_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name="id_usuario", unique=true, nullable=false) // ID del usuario asociado al cliente
    private long idUsuario;

    @Column(name="tipo_cliente", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoCliente tipoCliente;

    @Column(name="telefono", length=20)
    private String telefono;

    @Column(name="direccion", length=200)
    private String direccion;

    @Column(name="ciudad", length=100)
    private String ciudad;

    @Column(name = "documento_identidad_estado", length = 20)
    private String documentoIdentidadEstado;

    public enum TipoCliente{
        PERSONA_NATURAL,
        EMPRESA
    }
}
