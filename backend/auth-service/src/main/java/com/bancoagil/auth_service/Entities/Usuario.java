package com.bancoagil.auth_service.Entities;

import java.sql.Timestamp;

import com.bancoagil.auth_service.Enums.TipoUsuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que mapea la tabla 'usuarios'.
 * Contiene la información básica de acceso para todos los tipos de usuario.
 */
@Data // Aquí están tus Getters y Setters
@Builder // Genera un patrón de construcción de objetos más legible y seguro.
@NoArgsConstructor // Aquí está tu constructor vacío
@AllArgsConstructor // Aquí está tu constructor completo
@Entity
@Table(name = "usuarios")
public class Usuario {

    // Clave Primaria (serial)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Columna: email (varchar(100) NN)
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    // Columna: password (varchar(255) NN) -> Almacenar el hash de la contraseña
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // Columna: tipo_usuario (varchar(50) NN) -> Usando el Enum para seguridad de tipos
    @Enumerated(EnumType.STRING) // Guarda el nombre del enum (CLIENTE, ASESOR, etc.) como String
    @Column(name = "tipo_usuario", nullable = false, length = 50)
    private TipoUsuario tipoUsuario;

    // Columna: activo (boolean)
    @Column(name = "activo")
    private Boolean activo;

    // Columna: fecha_creacion (timestamp)
    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    // Columna: fecha_actualizacion (timestamp)
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cliente cliente;
    
}