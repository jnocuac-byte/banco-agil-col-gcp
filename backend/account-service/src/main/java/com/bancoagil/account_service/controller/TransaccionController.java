package com.bancoagil.account_service.controller;

import com.bancoagil.account_service.dto.*;
import com.bancoagil.account_service.model.Transaccion;
import com.bancoagil.account_service.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionController {
    
    private final TransaccionService transaccionService;
    
    //Realizar un depósito
    @PostMapping("/deposito")
    public ResponseEntity<TransaccionDTO> realizarDeposito(@Valid @RequestBody DepositoDTO depositoDTO) {
        try {
            TransaccionDTO transaccion = transaccionService.realizarDeposito(depositoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Realizar un retiro
    @PostMapping("/retiro")
    public ResponseEntity<TransaccionDTO> realizarRetiro(@Valid @RequestBody RetiroDTO retiroDTO) {
        try {
            TransaccionDTO transaccion = transaccionService.realizarRetiro(retiroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    //Realizar una transferencia
    @PostMapping("/transferencia")
    public ResponseEntity<TransaccionDTO> realizarTransferencia(@Valid @RequestBody TransferenciaDTO transferenciaDTO) {
        try {
            TransaccionDTO transaccion = transaccionService.realizarTransferencia(transferenciaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    //Obtener transacción por ID
    @GetMapping("/{id}")
    public ResponseEntity<TransaccionDTO> obtenerTransaccionPorId(@PathVariable Long id) {
        try {
            TransaccionDTO transaccion = transaccionService.obtenerTransaccionPorId(id);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Obtener todas las transacciones de una cuenta
    @GetMapping("/cuenta/{idCuenta}")
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorCuenta(@PathVariable Long idCuenta) {
        List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorCuenta(idCuenta);
        return ResponseEntity.ok(transacciones);
    }
    
    // Obtener transacciones de una cuenta con paginación

    @GetMapping("/cuenta/{idCuenta}/paginadas")
    public ResponseEntity<Page<TransaccionDTO>> obtenerTransaccionesPorCuentaPaginadas(
            @PathVariable Long idCuenta, 
            Pageable pageable) {
        Page<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorCuentaPaginadas(idCuenta, pageable);
        return ResponseEntity.ok(transacciones);
    }
    
    // Obtener últimas N transacciones de una cuenta
 
    @GetMapping("/cuenta/{idCuenta}/ultimas")
    public ResponseEntity<List<TransaccionDTO>> obtenerUltimasTransacciones(
            @PathVariable Long idCuenta,
            @RequestParam(defaultValue = "10") int limite) {
        List<TransaccionDTO> transacciones = transaccionService.obtenerUltimasTransaccionesPorCuenta(idCuenta, limite);
        return ResponseEntity.ok(transacciones);
    }
    
    //Obtener todas las transacciones
    @GetMapping
    public ResponseEntity<List<TransaccionDTO>> obtenerTodasLasTransacciones() {
        List<TransaccionDTO> transacciones = transaccionService.obtenerTodasLasTransacciones();
        return ResponseEntity.ok(transacciones);
    }
    
    //Obtener transacciones por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorTipo(@PathVariable Transaccion.TipoTransaccion tipo) {
        List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorTipo(tipo);
        return ResponseEntity.ok(transacciones);
    }
    
}
