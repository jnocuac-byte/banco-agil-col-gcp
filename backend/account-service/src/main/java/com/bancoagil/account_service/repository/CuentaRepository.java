package com.bancoagil.account_service.repository;

import com.bancoagil.account_service.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    // Buscar cuenta por número de cuenta
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    // Buscar todas las cuentas de un cliente
    List<Cuenta> findByIdCliente(Long idCliente);
    
    // Buscar cuentas activas de un cliente
    List<Cuenta> findByIdClienteAndEstado(Long idCliente, Cuenta.EstadoCuenta estado);
    
    // Verificar si existe una cuenta con ese numero
    boolean existsByNumeroCuenta(String numeroCuenta);
    
    // Buscar cuentas por tipo
    List<Cuenta> findByTipoCuenta(Cuenta.TipoCuenta tipoCuenta);
    
    // Buscar cuentas activas
    List<Cuenta> findByEstado(Cuenta.EstadoCuenta estado);
    
    // Contar cuentas por cliente
    @Query("SELECT COUNT(c) FROM Cuenta c WHERE c.idCliente = :idCliente")
    int countByIdCliente(@Param("idCliente") Long idCliente);
    
    // Buscar cuentas con saldo mayor a un monto específico
    @Query("SELECT c FROM Cuenta c WHERE c.saldoActual >= :saldoMinimo")
    List<Cuenta> findBySaldoActualGreaterThanEqual(@Param("saldoMinimo") java.math.BigDecimal saldoMinimo);
}
