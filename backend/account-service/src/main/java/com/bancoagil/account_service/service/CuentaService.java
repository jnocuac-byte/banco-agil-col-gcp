package com.bancoagil.account_service.service;

import com.bancoagil.account_service.dto.*;
import com.bancoagil.account_service.model.Cuenta;
import com.bancoagil.account_service.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService {
    
    private final CuentaRepository cuentaRepository;

    public CuentaDTO crearCuenta(CuentaCreateDTO cuentaCreateDTO) {
        // Validar que no exista una cuenta con el mismo número
        if (cuentaRepository.existsByNumeroCuenta(cuentaCreateDTO.getNumeroCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con el número: " + cuentaCreateDTO.getNumeroCuenta());
        }
        
        Cuenta cuenta = new Cuenta();
        cuenta.setIdCliente(cuentaCreateDTO.getIdCliente());
        cuenta.setNumeroCuenta(cuentaCreateDTO.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaCreateDTO.getTipoCuenta());
        cuenta.setSaldoActual(cuentaCreateDTO.getSaldoInicial());
        cuenta.setEstado(Cuenta.EstadoCuenta.ACTIVA);
        
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        
        
        return convertirADTO(cuentaGuardada);
    }
    
    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + id));
        return convertirADTO(cuenta);
    }

    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con número: " + numeroCuenta));
        return convertirADTO(cuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasPorCliente(Long idCliente) {
        List<Cuenta> cuentas = cuentaRepository.findByIdCliente(idCliente);
        return cuentas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
 
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasActivasPorCliente(Long idCliente) {
        List<Cuenta> cuentas = cuentaRepository.findByIdClienteAndEstado(idCliente, Cuenta.EstadoCuenta.ACTIVA);
        return cuentas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerTodasLasCuentas() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        return cuentas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public CuentaDTO bloquearCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con número: " + numeroCuenta));
        
        if (cuenta.getEstado() == Cuenta.EstadoCuenta.CERRADA) {
            throw new RuntimeException("No se puede bloquear una cuenta cerrada");
        }
        
        cuenta.setEstado(Cuenta.EstadoCuenta.BLOQUEADA);
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return convertirADTO(cuentaActualizada);
    }
    
    public CuentaDTO activarCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con número: " + numeroCuenta));
        
        if (cuenta.getEstado() == Cuenta.EstadoCuenta.CERRADA) {
            throw new RuntimeException("No se puede activar una cuenta cerrada");
        }
        
        cuenta.setEstado(Cuenta.EstadoCuenta.ACTIVA);
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return convertirADTO(cuentaActualizada);
    }

    public CuentaDTO cerrarCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con número: " + numeroCuenta));
        
        if (cuenta.getSaldoActual().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("No se puede cerrar una cuenta con saldo diferente a cero. Saldo actual: " + cuenta.getSaldoActual());
        }
        
        cuenta.setEstado(Cuenta.EstadoCuenta.CERRADA);
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return convertirADTO(cuentaActualizada);
    }

    public void actualizarSaldo(Long idCuenta, BigDecimal nuevoSaldo) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + idCuenta));
        
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El saldo no puede ser negativo");
        }
        
        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }
    
    public Cuenta obtenerCuentaEntityPorId(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + id));
    }

    public Cuenta obtenerCuentaEntityPorNumero(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con número: " + numeroCuenta));
    }
    
    private CuentaDTO convertirADTO(Cuenta cuenta) {
        CuentaDTO dto = new CuentaDTO();
        dto.setId(cuenta.getId());
        dto.setIdCliente(cuenta.getIdCliente());
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setTipoCuenta(cuenta.getTipoCuenta());
        dto.setSaldoActual(cuenta.getSaldoActual());
        dto.setEstado(cuenta.getEstado());
        dto.setFechaApertura(cuenta.getFechaApertura());
        return dto;
    }
}
