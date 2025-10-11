package com.bancoagil.auth_service.model;

import java.time.LocalDate;

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

// Entidad que representa a una persona natural
@Entity
@Table(name="personas_naturales") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class PersonaNatural {
    
    // Identificador único de la persona natural
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    // Id del cliente asociado a la persona natural
    @Column(name="id_cliente", unique=true, nullable=false)
    private Long idCliente;

    // Relación uno a uno con la entidad Cliente
    @OneToOne
    @JoinColumn(name="id_cliente", insertable=false, updatable=false) // Mapea la columna id_cliente
    private Cliente cliente;

    // Información específica de la persona natural
    @Column(name="num_documento", length=20, unique=true, nullable=false)
    private String numDocumento;

    // Tipo de documento de la persona natural
    @Column(name="tipo_documento", length=20, nullable=false)
    @Enumerated(EnumType.STRING) // Almacena el enum como cadena en la base de datos
    private TipoDocumento tipoDocumento;

    // Nombres, apellidos y fecha de nacimiento de la persona natural
    @Column(name="nombres", length=100, nullable=false)
    private String nombres;

    // Apellidos de la persona natural
    @Column(name="apellidos", length=100, nullable=false)
    private String apellidos;

    // Fecha de nacimiento de la persona natural
    @Column(name="fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // Tipo de documento (CC, CE, PASAPORTE)
    public enum TipoDocumento{
        CC,
        CE,
        PASAPORTE
    }

}
