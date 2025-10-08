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

@Entity
@Table(name = "asesores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asesor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long idUsuario;
    
    @OneToOne
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;
    
    @Column(nullable = false, length = 100)
    private String nombres;
    
    @Column(nullable = false, length = 100)
    private String apellidos;
    
    @Column(name = "codigo_empleado", unique = true, length = 20)
    private String codigoEmpleado;
    
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private Area area;
    
    public enum Area {
        CREDITO,
        RIESGO,
        ADMINISTRACION
    }
}