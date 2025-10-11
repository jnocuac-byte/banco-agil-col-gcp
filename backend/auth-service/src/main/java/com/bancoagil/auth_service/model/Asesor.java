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

// Entidad que representa a un asesor
@Entity
@Table(name = "asesores") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class Asesor {
    
    // Identificador único del asesor
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;
    
    // ID del usuario asociado al asesor
    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long idUsuario;
    
    // Relación uno a uno con la entidad Usuario
    @OneToOne
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false) // Mapea la columna id_usuario
    private Usuario usuario;
    
    // Nombres del asesor
    @Column(nullable = false, length = 100)
    private String nombres;
    
    // Apellidos del asesor
    @Column(nullable = false, length = 100)
    private String apellidos;
    
    // Código del empleado
    @Column(name = "codigo_empleado", unique = true, length = 20)
    private String codigoEmpleado;
    
    // Área del asesor (CREDITO, RIESGO, ADMINISTRACION, ADMIN_TOTAL)
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private Area area;
    
    // Estado del asesor (activo/inactivo)
    public enum Area {
        CREDITO,
        RIESGO,
        ADMINISTRACION,
        ADMIN_TOTAL
    }
}