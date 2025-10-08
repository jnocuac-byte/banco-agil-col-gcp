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
@Table(name="clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(name="id_usuario", unique=true, nullable=false)
    private long idUsuario;

    @OneToOne
    @JoinColumn(name="id_usuario", insertable=false, updatable=false)
    private Usuario usuario;

    @Column(name="tipo_cliente")
    @Enumerated(EnumType.STRING)
    private TipoCliente tipoCliente;

    @Column(name="telefono", length=20)
    private String telefono;

    @Column(name="direccion", length=200)
    private String direccion;

    @Column(name="ciudad", length=100)
    private String ciudad;

    public enum TipoCliente{
        PERSONA_NATURAL,
        EMPRESA
    }

}
