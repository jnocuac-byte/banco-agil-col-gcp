package com.bancoagil.auth_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuentas")
@Data
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
    
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVA;
    
    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;
    
    @PrePersist
    protected void onCreate() {
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }
        if (saldoActual == null) {
            saldoActual = BigDecimal.ZERO;
        }
        if (estado == null) {
            estado = Estado.ACTIVA;
        }
    }
    
    public enum TipoCuenta {
        AHORROS,
        CORRIENTE
    }
    
    public enum Estado {
        ACTIVA,
        BLOQUEADA,
        CERRADA
    }
}