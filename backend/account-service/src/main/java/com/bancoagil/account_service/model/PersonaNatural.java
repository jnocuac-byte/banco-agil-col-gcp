package com.bancoagil.account_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "personas_naturales")
@Getter
@Setter
public class PersonaNatural {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", unique = true, nullable = false)
    private Long idCliente;

    @Column(name = "num_documento", unique = true, nullable = false, length = 20)
    private String numDocumento;

    @Column(name = "tipo_documento", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @OneToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id", insertable = false, updatable = false)
    private Cliente cliente;

    public enum TipoDocumento {
        CC, CE, PASAPORTE
    }
}
