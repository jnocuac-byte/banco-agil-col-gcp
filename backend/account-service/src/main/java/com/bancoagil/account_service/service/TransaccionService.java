package com.bancoagil.account_service.service;

import com.bancoagil.account_service.dto.*;
import com.bancoagil.account_service.model.Cuenta;
import com.bancoagil.account_service.model.Transaccion;
import com.bancoagil.account_service.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransaccionService {
    
    private final TransaccionRepository transaccionRepository;
    private final CuentaService cuentaService;

    public TransaccionDTO realizarDeposito(DepositoDTO depositoDTO) {
        Cuenta cuenta = cuentaService.obtenerCuentaEntityPorNumero(depositoDTO.getNumeroCuenta());
        
        // Validar que la cuenta esté activa
        if (cuenta.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
            throw new RuntimeException("La cuenta no está activa para realizar transacciones");
        }
        
        // Crear la transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaDestino(cuenta);
        transaccion.setTipoTransaccion(Transaccion.TipoTransaccion.DEPOSITO);
        transaccion.setMonto(depositoDTO.getMonto());
        transaccion.setDescripcion(depositoDTO.getDescripcion());
        transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
        
        // Guardar la transacción
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        
        // Actualizar el saldo de la cuenta
        BigDecimal nuevoSaldo = cuenta.getSaldoActual().add(depositoDTO.getMonto());
        cuentaService.actualizarSaldo(cuenta.getId(), nuevoSaldo);
        
        return convertirADTO(transaccionGuardada);
    }

    public TransaccionDTO realizarRetiro(RetiroDTO retiroDTO) {
        Cuenta cuenta = cuentaService.obtenerCuentaEntityPorNumero(retiroDTO.getNumeroCuenta());
        
        // Validar que la cuenta esté activa
        if (cuenta.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
            throw new RuntimeException("La cuenta no está activa para realizar transacciones");
        }
        
        // Validar que tenga saldo suficiente
        if (cuenta.getSaldoActual().compareTo(retiroDTO.getMonto()) < 0) {
            throw new RuntimeException("Saldo insuficiente. Saldo disponible: " + cuenta.getSaldoActual());
        }
        
        // Crear la transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigen(cuenta);
        transaccion.setTipoTransaccion(Transaccion.TipoTransaccion.RETIRO);
        transaccion.setMonto(retiroDTO.getMonto());
        transaccion.setDescripcion(retiroDTO.getDescripcion());
        transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
        
        // Guardar la transacción
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        
        // Actualizar el saldo de la cuenta
        BigDecimal nuevoSaldo = cuenta.getSaldoActual().subtract(retiroDTO.getMonto());
        cuentaService.actualizarSaldo(cuenta.getId(), nuevoSaldo);
        
        return convertirADTO(transaccionGuardada);
    }
    

    public TransaccionDTO realizarTransferencia(TransferenciaDTO transferenciaDTO) {
        // Validar que las cuentas sean diferentes
        if (transferenciaDTO.getNumeroCuentaOrigen().equals(transferenciaDTO.getNumeroCuentaDestino())) {
            throw new RuntimeException("No se puede transferir a la misma cuenta");
        }
        
        Cuenta cuentaOrigen = cuentaService.obtenerCuentaEntityPorNumero(transferenciaDTO.getNumeroCuentaOrigen());
        Cuenta cuentaDestino = cuentaService.obtenerCuentaEntityPorNumero(transferenciaDTO.getNumeroCuentaDestino());
        
        // Validar que ambas cuentas estén activas
        if (cuentaOrigen.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
            throw new RuntimeException("La cuenta origen no está activa para realizar transacciones");
        }
        if (cuentaDestino.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
            throw new RuntimeException("La cuenta destino no está activa para realizar transacciones");
        }
        
        // Validar que la cuenta origen tenga saldo suficiente
        if (cuentaOrigen.getSaldoActual().compareTo(transferenciaDTO.getMonto()) < 0) {
            throw new RuntimeException("Saldo insuficiente en cuenta origen. Saldo disponible: " + cuentaOrigen.getSaldoActual());
        }
        
        // Crear la transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigen(cuentaOrigen);
        transaccion.setCuentaDestino(cuentaDestino);
        transaccion.setTipoTransaccion(Transaccion.TipoTransaccion.TRANSFERENCIA);
        transaccion.setMonto(transferenciaDTO.getMonto());
        transaccion.setDescripcion(transferenciaDTO.getDescripcion());
        transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
        
        // Guardar la transacción
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        
        // Actualizar los saldos
        BigDecimal nuevoSaldoOrigen = cuentaOrigen.getSaldoActual().subtract(transferenciaDTO.getMonto());
        BigDecimal nuevoSaldoDestino = cuentaDestino.getSaldoActual().add(transferenciaDTO.getMonto());
        
        cuentaService.actualizarSaldo(cuentaOrigen.getId(), nuevoSaldoOrigen);
        cuentaService.actualizarSaldo(cuentaDestino.getId(), nuevoSaldoDestino);
        
        return convertirADTO(transaccionGuardada);
    }

    @Transactional(readOnly = true)
    public TransaccionDTO obtenerTransaccionPorId(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con ID: " + id));
        return convertirADTO(transaccion);
    }
    

    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorCuenta(Long idCuenta) {
        List<Transaccion> transacciones = transaccionRepository.findByCuentaOrigenIdOrCuentaDestinoIdOrderByFechaTransaccionDesc(idCuenta);
        return transacciones.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<TransaccionDTO> obtenerTransaccionesPorCuentaPaginadas(Long idCuenta, Pageable pageable) {
        Page<Transaccion> transacciones = transaccionRepository.findByCuentaOrigenIdOrCuentaDestinoId(idCuenta, pageable);
        return transacciones.map(this::convertirADTO);
    }
    
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerUltimasTransaccionesPorCuenta(Long idCuenta, int limite) {
        List<Transaccion> transacciones = transaccionRepository.findUltimasTransaccionesDeCuenta(idCuenta, limite);
        return transacciones.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTodasLasTransacciones() {
        List<Transaccion> transacciones = transaccionRepository.findAll();
        return transacciones.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularIngresosTotales(Long idCuenta) {
        return transaccionRepository.calcularIngresosTotales(idCuenta);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularEgresosTotales(Long idCuenta) {
        return transaccionRepository.calcularEgresosTotales(idCuenta);
    }

    @Transactional(readOnly = true)
    public Integer contarTransaccionesDeCuenta(Long idCuenta) {
        return transaccionRepository.contarTransaccionesDeCuenta(idCuenta);
    }

    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorTipo(Transaccion.TipoTransaccion tipo) {
        List<Transaccion> transacciones = transaccionRepository.findByTipoTransaccionOrderByFechaTransaccionDesc(tipo);
        return transacciones.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private TransaccionDTO convertirADTO(Transaccion transaccion) {
        TransaccionDTO dto = new TransaccionDTO();
        dto.setId(transaccion.getId());
        
        if (transaccion.getCuentaOrigen() != null) {
            dto.setIdCuentaOrigen(transaccion.getCuentaOrigen().getId());
            dto.setNumeroCuentaOrigen(transaccion.getCuentaOrigen().getNumeroCuenta());
        }
        
        if (transaccion.getCuentaDestino() != null) {
            dto.setIdCuentaDestino(transaccion.getCuentaDestino().getId());
            dto.setNumeroCuentaDestino(transaccion.getCuentaDestino().getNumeroCuenta());
        }
        
        dto.setTipoTransaccion(transaccion.getTipoTransaccion());
        dto.setMonto(transaccion.getMonto());
        dto.setDescripcion(transaccion.getDescripcion());
        dto.setFechaTransaccion(transaccion.getFechaTransaccion());
        dto.setEstado(transaccion.getEstado());
        
        return dto;
    }

    /**
     * Registrar desembolso de crédito
     * Se usa cuando se aprueba una solicitud de crédito
     */
    public TransaccionDTO registrarDesembolsoCredito(DesembolsoDTO desembolsoDTO) {
        // Obtener cuenta destino
        Cuenta cuenta = cuentaService.obtenerCuentaEntityPorId(desembolsoDTO.getIdCuenta());
        
        // Validar que la cuenta esté activa
        if (cuenta.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
            throw new RuntimeException("La cuenta no está activa para recibir desembolsos");
        }
        
        // Crear la transacción de desembolso
        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaDestino(cuenta);
        transaccion.setTipoTransaccion(Transaccion.TipoTransaccion.DESEMBOLSO_CREDITO);
        transaccion.setMonto(desembolsoDTO.getMonto());
        transaccion.setDescripcion(desembolsoDTO.getDescripcion() != null ? 
            desembolsoDTO.getDescripcion() : 
            "Desembolso de crédito - Solicitud #" + desembolsoDTO.getIdSolicitud());
        transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
        
        // Guardar la transacción
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        
        // Actualizar el saldo de la cuenta (sumar el monto del crédito)
        BigDecimal nuevoSaldo = cuenta.getSaldoActual().add(desembolsoDTO.getMonto());
        cuentaService.actualizarSaldo(cuenta.getId(), nuevoSaldo);
        
        return convertirADTO(transaccionGuardada);
    }

    /**
     * Registrar pago de cuota de crédito
     * Se usa cuando el cliente paga una cuota mensual
     */
    public TransaccionDTO registrarPagoCredito(Long idCuenta, BigDecimal monto, String descripcion) {
        // Obtener cuenta origen
        Cuenta cuenta = cuentaService.obtenerCuentaEntityPorId(idCuenta);
        
        // Validar que la cuenta esté activa
        if (cuenta.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
            throw new RuntimeException("La cuenta no está activa para realizar pagos");
        }
        
        // Validar que tenga saldo suficiente
        if (cuenta.getSaldoActual().compareTo(monto) < 0) {
            throw new RuntimeException("Saldo insuficiente para pagar la cuota. Saldo disponible: " + cuenta.getSaldoActual());
        }
        
        // Crear la transacción de pago
        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigen(cuenta);
        transaccion.setTipoTransaccion(Transaccion.TipoTransaccion.PAGO_CREDITO);
        transaccion.setMonto(monto);
        transaccion.setDescripcion(descripcion);
        transaccion.setEstado(Transaccion.EstadoTransaccion.COMPLETADA);
        
        // Guardar la transacción
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        
        // Actualizar el saldo de la cuenta (restar el monto pagado)
        BigDecimal nuevoSaldo = cuenta.getSaldoActual().subtract(monto);
        cuentaService.actualizarSaldo(cuenta.getId(), nuevoSaldo);
        
        return convertirADTO(transaccionGuardada);
    }

}
