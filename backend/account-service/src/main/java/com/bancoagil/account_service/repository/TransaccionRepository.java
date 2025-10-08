package com.bancoagil.account_service.repository;

import com.bancoagil.account_service.model.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    
    // Buscar transacciones por cuenta origen
    List<Transaccion> findByCuentaOrigenIdOrderByFechaTransaccionDesc(Long idCuentaOrigen);
    
    // Buscar transacciones por cuenta destino
    List<Transaccion> findByCuentaDestinoIdOrderByFechaTransaccionDesc(Long idCuentaDestino);
    
    // Buscar todas las transacciones 
    @Query("SELECT t FROM Transaccion t WHERE t.cuentaOrigen.id = :idCuenta OR t.cuentaDestino.id = :idCuenta ORDER BY t.fechaTransaccion DESC")
    List<Transaccion> findByCuentaOrigenIdOrCuentaDestinoIdOrderByFechaTransaccionDesc(@Param("idCuenta") Long idCuenta);
    
    // Buscar transacciones de una cuenta con paginación
    @Query("SELECT t FROM Transaccion t WHERE t.cuentaOrigen.id = :idCuenta OR t.cuentaDestino.id = :idCuenta ORDER BY t.fechaTransaccion DESC")
    Page<Transaccion> findByCuentaOrigenIdOrCuentaDestinoId(@Param("idCuenta") Long idCuenta, Pageable pageable);
    
    // Buscar transacciones por tipo
    List<Transaccion> findByTipoTransaccionOrderByFechaTransaccionDesc(Transaccion.TipoTransaccion tipoTransaccion);
    
    // Buscar transacciones por estado
    List<Transaccion> findByEstadoOrderByFechaTransaccionDesc(Transaccion.EstadoTransaccion estado);
    
    // Buscar transacciones por rango de fechas
    @Query("SELECT t FROM Transaccion t WHERE t.fechaTransaccion BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fechaTransaccion DESC")
    List<Transaccion> findByFechaTransaccionBetween(@Param("fechaInicio") ZonedDateTime fechaInicio, @Param("fechaFin") ZonedDateTime fechaFin);
    
    // Obtener últimas N transacciones de una cuenta
    @Query(value = "SELECT t.* FROM transacciones t WHERE t.id_cuenta_origen = :idCuenta OR t.id_cuenta_destino = :idCuenta ORDER BY t.fecha_transaccion DESC LIMIT :limite", nativeQuery = true)
    List<Transaccion> findUltimasTransaccionesDeCuenta(@Param("idCuenta") Long idCuenta, @Param("limite") int limite);
    
    // Sumar ingresos de una cuenta)
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE (t.cuentaDestino.id = :idCuenta AND t.tipoTransaccion IN ('DEPOSITO', 'TRANSFERENCIA', 'DESEMBOLSO_CREDITO')) AND t.estado = 'COMPLETADA'")
    BigDecimal calcularIngresosTotales(@Param("idCuenta") Long idCuenta);
    
    // Sumar egresos de una cuenta
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE (t.cuentaOrigen.id = :idCuenta AND t.tipoTransaccion IN ('RETIRO', 'TRANSFERENCIA', 'PAGO_CREDITO')) AND t.estado = 'COMPLETADA'")
    BigDecimal calcularEgresosTotales(@Param("idCuenta") Long idCuenta);
    
    // Contar transacciones de una cuenta
    @Query("SELECT COUNT(t) FROM Transaccion t WHERE t.cuentaOrigen.id = :idCuenta OR t.cuentaDestino.id = :idCuenta")
    int contarTransaccionesDeCuenta(@Param("idCuenta") Long idCuenta);
}
