package com.bancoagil.account_service.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancoagil.account_service.dto.CuentaCreateDTO;
import com.bancoagil.account_service.dto.CuentaDTO;
import com.bancoagil.account_service.dto.CuentaResumenDTO;
import com.bancoagil.account_service.dto.DesembolsoDTO;
import com.bancoagil.account_service.dto.TransaccionDTO;
import com.bancoagil.account_service.model.Cuenta;
import com.bancoagil.account_service.service.CuentaResumenService;
import com.bancoagil.account_service.service.CuentaService;
import com.bancoagil.account_service.service.TransaccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    
    @Autowired
    private final CuentaService cuentaService;

    @Autowired
    private final CuentaResumenService cuentaResumenService;

    @Autowired
    private final TransaccionService transaccionService;

    @PostMapping
    public ResponseEntity<CuentaDTO> crearCuenta(@Valid @RequestBody CuentaCreateDTO cuentaCreateDTO) {
        try {
            CuentaDTO cuentaCreada = cuentaService.crearCuenta(cuentaCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaDTO> obtenerCuentaPorId(@PathVariable Long id) {
        try {
            CuentaDTO cuenta = cuentaService.obtenerCuentaPorId(id);
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> obtenerCuentaPorNumero(@PathVariable String numeroCuenta) {
        try {
            CuentaDTO cuenta = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<CuentaDTO>> obtenerCuentasPorCliente(@PathVariable Long idCliente) {
        List<CuentaDTO> cuentas = cuentaService.obtenerCuentasPorCliente(idCliente);
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/cliente/{idCliente}/activas")
    public ResponseEntity<List<CuentaDTO>> obtenerCuentasActivasPorCliente(@PathVariable Long idCliente) {
        List<CuentaDTO> cuentas = cuentaService.obtenerCuentasActivasPorCliente(idCliente);
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping
    public ResponseEntity<List<CuentaDTO>> obtenerTodasLasCuentas() {
        List<CuentaDTO> cuentas = cuentaService.obtenerTodasLasCuentas();
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/numero/{numeroCuenta}/resumen")
    public ResponseEntity<CuentaResumenDTO> obtenerResumenCuenta(@PathVariable String numeroCuenta) {
        try {
            CuentaResumenDTO resumen = cuentaResumenService.obtenerResumenCuenta(numeroCuenta);
            return ResponseEntity.ok(resumen);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/numero/{numeroCuenta}/bloquear")
    public ResponseEntity<CuentaDTO> bloquearCuenta(@PathVariable String numeroCuenta) {
        try {
            CuentaDTO cuenta = cuentaService.bloquearCuenta(numeroCuenta);
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
 
    @PutMapping("/numero/{numeroCuenta}/activar")
    public ResponseEntity<CuentaDTO> activarCuenta(@PathVariable String numeroCuenta) {
        try {
            CuentaDTO cuenta = cuentaService.activarCuenta(numeroCuenta);
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/numero/{numeroCuenta}/cerrar")
    public ResponseEntity<CuentaDTO> cerrarCuenta(@PathVariable String numeroCuenta) {
        try {
            CuentaDTO cuenta = cuentaService.cerrarCuenta(numeroCuenta);
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
 * Obtener cuenta de ahorros activa de un cliente
 * Se usa cuando se aprueba un crédito para saber dónde depositar
 */
    @GetMapping("/cliente/{idCliente}/ahorros")
    public ResponseEntity<CuentaDTO> obtenerCuentaAhorrosCliente(@PathVariable Long idCliente) {
        try {
            // Obtener todas las cuentas activas del cliente
            List<CuentaDTO> cuentas = cuentaService.obtenerCuentasActivasPorCliente(idCliente);
            
            // Buscar cuenta de ahorros activa
            CuentaDTO cuentaAhorros = cuentas.stream()
                .filter(c -> c.getTipoCuenta() == Cuenta.TipoCuenta.AHORROS)
                .findFirst()
                .orElse(null);
            
            if (cuentaAhorros == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(cuentaAhorros);
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/desembolso")
    public ResponseEntity<Map<String, Object>> registrarDesembolso(
            @PathVariable Long id,
            @Valid @RequestBody DesembolsoDTO desembolsoDTO) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Asegurar que el ID de la URL coincida con el del DTO
            desembolsoDTO.setIdCuenta(id);
            
            // Registrar el desembolso
            TransaccionDTO transaccion = transaccionService.registrarDesembolsoCredito(desembolsoDTO);
            
            // Obtener el nuevo saldo de la cuenta
            CuentaDTO cuenta = cuentaService.obtenerCuentaPorId(id);
            
            response.put("success", true);
            response.put("message", "Desembolso registrado exitosamente");
            response.put("transaccionId", transaccion.getId());
            response.put("nuevoSaldo", cuenta.getSaldoActual());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Endpoint para registrar pago de cuota de crédito
     * Se usa cuando el cliente paga una cuota mensual
     */
    @PostMapping("/{id}/pago-credito")
    public ResponseEntity<Map<String, Object>> registrarPagoCredito(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extraer datos del request
            if (!request.containsKey("monto") || !request.containsKey("descripcion")) {
                throw new RuntimeException("Se requiere monto y descripción");
            }
            
            BigDecimal monto = new BigDecimal(request.get("monto").toString());
            String descripcion = request.get("descripcion").toString();
            
            // Registrar el pago
            TransaccionDTO transaccion = transaccionService.registrarPagoCredito(id, monto, descripcion);
            
            // Obtener el nuevo saldo de la cuenta
            CuentaDTO cuenta = cuentaService.obtenerCuentaPorId(id);
            
            response.put("success", true);
            response.put("message", "Pago de crédito registrado exitosamente");
            response.put("transaccionId", transaccion.getId());
            response.put("nuevoSaldo", cuenta.getSaldoActual());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
