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

// Entidad para representar una cuenta bancaria
@Entity
@Table(name = "cuentas") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class Cuenta {
    
    // Identificador único de la cuenta
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;
    
    // Id del cliente propietario de la cuenta
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;
    
    // Número único de la cuenta
    @Column(name = "numero_cuenta", nullable = false, unique = true, length = 20)
    private String numeroCuenta;
    
    // Tipo de cuenta (AHORROS, CORRIENTE)
    @Column(name = "tipo_cuenta", nullable = false, length = 20)
    @Enumerated(EnumType.STRING) // Almacena el enum como cadena en la base de datos
    private TipoCuenta tipoCuenta;
    
    // Saldo actual de la cuenta
    @Column(name = "saldo_actual", precision = 15, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;
    
    // Estado de la cuenta (ACTIVA, BLOQUEADA, CERRADA)
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING) // Almacena el enum como cadena en la base de datos
    private Estado estado = Estado.ACTIVA;
    
    // Fecha de apertura de la cuenta
    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;
    
    // Fecha de cierre de la cuenta (si aplica)
    @PrePersist
    protected void onCreate() {
        // Inicializar fecha de apertura si no está establecida
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }
        // Inicializar saldo actual y estado si no están establecidos
        if (saldoActual == null) {
            saldoActual = BigDecimal.ZERO;
        }
        // Inicializar estado si no está establecido
        if (estado == null) {
            estado = Estado.ACTIVA;
        }
    }
    
    // Enumeración para los tipos de cuenta
    public enum TipoCuenta {
        AHORROS,
        CORRIENTE
    }
    
    // Enumeración para los estados de la cuenta
    public enum Estado {
        ACTIVA,
        BLOQUEADA,
        CERRADA
    }
}