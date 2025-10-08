package com.bancoagil.account_service.controller;

import com.bancoagil.account_service.dto.ClienteDTO;
import com.bancoagil.account_service.dto.ClienteCompletoDTO;
import com.bancoagil.account_service.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Obtener todos los clientes 
    @GetMapping("/clientes")
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.getAllClientes());
    }

    // Obtener todos los clientes con información completa
    @GetMapping("/clientes/completos")
    public ResponseEntity<List<ClienteCompletoDTO>> listarClientesCompletos() {
        return ResponseEntity.ok(clienteService.getAllClientesCompletos());
    }

    // Obtener cliente por ID del usuario
    @GetMapping("/clientes/usuario/{idUsuario}")
    public ResponseEntity<ClienteDTO> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        try {
            ClienteDTO cliente = clienteService.getClienteByIdUsuario(idUsuario);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener cliente completo por ID del usuario
    @GetMapping("/clientes/completo/usuario/{idUsuario}")
    public ResponseEntity<ClienteCompletoDTO> obtenerCompleto(@PathVariable Integer idUsuario) {
        try {
            ClienteDTO cliente = clienteService.getClienteByIdUsuario(idUsuario);
            ClienteCompletoDTO clienteCompleto = clienteService.getClienteCompletoById(cliente.getId());
            return ResponseEntity.ok(clienteCompleto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener cliente por ID
    @GetMapping("/clientes/{id}")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
        try {
            ClienteDTO cliente = clienteService.getClienteById(id);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener cliente completo por ID
    @GetMapping("/clientes/completo/{id}")
    public ResponseEntity<ClienteCompletoDTO> obtenerCompletoPorId(@PathVariable Long id) {
        try {
            ClienteCompletoDTO cliente = clienteService.getClienteCompletoById(id);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar información del cliente
    @PutMapping("/clientes/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDTO clienteActualizado = clienteService.updateCliente(id, clienteDTO);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
