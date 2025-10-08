package com.bancoagil.account_service.controller;

import com.bancoagil.account_service.dto.CuentaCreateDTO;
import com.bancoagil.account_service.dto.CuentaDTO;
import com.bancoagil.account_service.dto.CuentaResumenDTO;
import com.bancoagil.account_service.service.CuentaService;
import com.bancoagil.account_service.service.CuentaResumenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    
    private final CuentaService cuentaService;
    private final CuentaResumenService cuentaResumenService;

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
}
