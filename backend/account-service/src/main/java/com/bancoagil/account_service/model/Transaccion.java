package com.bancoagil.account_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "transacciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_origen")
    private Cuenta cuentaOrigen;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_destino")
    private Cuenta cuentaDestino;
    
    @Column(name = "tipo_transaccion", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipoTransaccion;
    
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal monto;
    
    @Column(length = 255)
    private String descripcion;
    
    @Column(name = "fecha_transaccion")
    @CreationTimestamp
    private ZonedDateTime fechaTransaccion;
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado = EstadoTransaccion.COMPLETADA;
    
    public enum TipoTransaccion {
        DEPOSITO, 
        RETIRO, 
        TRANSFERENCIA, 
        DESEMBOLSO_CREDITO, 
        PAGO_CREDITO
    }
    
    public enum EstadoTransaccion {
        COMPLETADA, 
        FALLIDA, 
        PENDIENTE
    }
}
