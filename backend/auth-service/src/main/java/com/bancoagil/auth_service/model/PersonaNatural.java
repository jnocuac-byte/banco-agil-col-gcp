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

@Entity
@Table(name="personas_naturales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaNatural {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="id_cliente", unique=true, nullable=false)
    private Long idCliente;

    @OneToOne
    @JoinColumn(name="id_cliente", insertable=false, updatable=false)
    private Cliente cliente;

    @Column(name="num_documento", length=20, unique=true, nullable=false)
    private String numDocumento;

    @Column(name="tipo_documento", length=20, nullable=false)
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Column(name="nombres", length=100, nullable=false)
    private String nombres;

    @Column(name="apellidos", length=100, nullable=false)
    private String apellidos;

    @Column(name="fecha_nacimiento")
    private LocalDate fechaNacimiento;

    public enum TipoDocumento{
        CC,
        CE,
        PASAPORTE
    }

}
