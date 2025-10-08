package com.bancoagil.auth_service.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancoagil.auth_service.Dtos.ClienteCreateDTO;
import com.bancoagil.auth_service.Dtos.ClienteResponseDTO;
import com.bancoagil.auth_service.Dtos.ClienteUpdateDTO;
import com.bancoagil.auth_service.ServiceInterface.IClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService clienteService;

    // --- 1. CREAR CLIENTE ---
    // POST /api/v1/clientes
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> createCliente(
            @Valid @RequestBody ClienteCreateDTO clienteDto) {
        
        ClienteResponseDTO newCliente = clienteService.createCliente(clienteDto);
        return new ResponseEntity<>(newCliente, HttpStatus.CREATED);
    }

    // --- 2. OBTENER TODOS ---
    // GET /api/v1/clientes
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> getAllClientes() {
        List<ClienteResponseDTO> clientes = clienteService.getAllClientes();
        return ResponseEntity.ok(clientes);
    }

    // --- 3. OBTENER POR ID ---
    // GET /api/v1/clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getClienteById(@PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    // --- 4. ACTUALIZAR ---
    // PUT /api/v1/clientes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> updateCliente(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteUpdateDTO clienteDto) {
        
        ClienteResponseDTO updatedCliente = clienteService.updateCliente(id, clienteDto);
        return ResponseEntity.ok(updatedCliente);
    }

    // --- 5. ELIMINAR ---
    // DELETE /api/v1/clientes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Integer id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}