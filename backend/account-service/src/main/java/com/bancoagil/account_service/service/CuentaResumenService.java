package com.bancoagil.account_service.service;

import com.bancoagil.account_service.dto.CuentaResumenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CuentaResumenService {
    
    private final CuentaService cuentaService;
    private final TransaccionService transaccionService;
    
    public CuentaResumenDTO obtenerResumenCuenta(String numeroCuenta) {
        var cuenta = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
        
        CuentaResumenDTO resumen = new CuentaResumenDTO();
        resumen.setCuenta(cuenta);
        
        // Obtener estadísticas de transacciones
        resumen.setUltimasTransacciones(
            transaccionService.obtenerUltimasTransaccionesPorCuenta(cuenta.getId(), 10)
        );
        resumen.setTotalIngresos(
            transaccionService.calcularIngresosTotales(cuenta.getId())
        );
        resumen.setTotalEgresos(
            transaccionService.calcularEgresosTotales(cuenta.getId())
        );
        resumen.setCantidadTransacciones(
            transaccionService.contarTransaccionesDeCuenta(cuenta.getId())
        );
        
        return resumen;
    }
}
