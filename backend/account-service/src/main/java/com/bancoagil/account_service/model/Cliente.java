package com.bancoagil.account_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false, unique = true)
    private Integer idUsuario;

    @Column(name = "tipo_cliente", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoCliente tipoCliente;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", insertable = false, updatable = false)
    private Usuario usuario;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private PersonaNatural personaNatural;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Empresa empresa;

    public enum TipoCliente {
        PERSONA_NATURAL, EMPRESA
    }
}
