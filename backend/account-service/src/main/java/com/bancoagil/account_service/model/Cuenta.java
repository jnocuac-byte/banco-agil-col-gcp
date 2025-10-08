package com.bancoagil.account_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "cuentas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;
    
    @Column(name = "numero_cuenta", nullable = false, unique = true, length = 20)
    private String numeroCuenta;
    
    @Column(name = "tipo_cuenta", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;
    
    @Column(name = "saldo_actual", precision = 15, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoCuenta estado = EstadoCuenta.ACTIVA;
    
    @Column(name = "fecha_apertura")
    @CreationTimestamp
    private ZonedDateTime fechaApertura;

    @OneToMany(mappedBy = "cuentaOrigen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transaccionesOrigen;
    
    @OneToMany(mappedBy = "cuentaDestino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transaccionesDestino;
    
    public enum TipoCuenta {
        AHORROS, CORRIENTE
    }
    
    public enum EstadoCuenta {
        ACTIVA, BLOQUEADA, CERRADA
    }
}
