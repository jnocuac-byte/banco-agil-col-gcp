package com.bancoagil.auth_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad para representar un usuario del sistema (cliente o asesor)
@Entity
@Table(name="usuarios") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class Usuario {
    
    // Identificador único del usuario
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    // Nombre de usuario único
    @Column(name="email", length=100, unique=true, nullable=false)
    private String email;

    // Contraseña del usuario (almacenada de forma segura)
    @Column(name="password", length=255, nullable=false)
    private String password;

    // Tipo de usuario (cliente o asesor)
    @Column(name="tipo_usuario", length=20, nullable=false)
    @Enumerated(EnumType.STRING) // Almacena el enum como cadena en la base de datos
    private TipoUsuario tipoUsuario;

    // Estado del usuario (activo/inactivo)
    @Column(name="activo")
    private Boolean activo = true;

    // Tiempos de auditoría
    @Column(name="fecha_creacion", nullable=false, updatable=false)
    private LocalDateTime fechaCreacion;

    // Fecha de actualización del usuario
    @Column(name="fecha_actualizacion", nullable=false)
    private LocalDateTime fechaActualizacion;

    // Método que se ejecuta antes de persistir el usuario
    @PrePersist
    protected void onCreate(){
        fechaCreacion = LocalDateTime.now(); // Establece la fecha de creación
        fechaActualizacion = LocalDateTime.now(); // Establece la fecha de actualización
    }

    // Método que se ejecuta antes de actualizar el usuario
    @PreUpdate
    protected void onUpdate(){
        fechaActualizacion = LocalDateTime.now(); // Establece la fecha de actualización
    }

    // Tipos de usuario en el sistema
    public enum TipoUsuario{
        CLIENTE,
        ASESOR
    }
}
