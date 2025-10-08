package com.bancoagil.auth_service.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancoagil.auth_service.Dtos.EmpresaCreateDTO;
import com.bancoagil.auth_service.Dtos.EmpresaResponseDTO;
import com.bancoagil.auth_service.Dtos.EmpresaUpdateDTO;
import com.bancoagil.auth_service.ServiceInterface.IEmpresaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/empresas") // Endpoint coherente con el recurso
@RequiredArgsConstructor 
public class EmpresaController {

    private final IEmpresaService empresaService;

    // 1. ENDPOINT DE CREACIÓN (POST)
    /**
     * Registra la información de detalle de una Empresa y la asocia a un Cliente existente.
     * URL: POST /api/v1/empresas
     */
    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> createEmpresa(
        @Valid @RequestBody EmpresaCreateDTO dto) {
        
        EmpresaResponseDTO response = empresaService.create(dto);
        
        // Retornamos 201 Created
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. ENDPOINT DE LECTURA (GET)
    /**
     * Obtiene la información de una Empresa por su ID (PK de la tabla empresas).
     * URL: GET /api/v1/empresas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> getEmpresaById(@PathVariable Integer id) {
        
        EmpresaResponseDTO response = empresaService.findById(id);
        
        return ResponseEntity.ok(response);
    }
    
    // 3. ENDPOINT DE ACTUALIZACIÓN (PUT)
    /**
     * Actualiza la información de detalle de una Empresa.
     * URL: PUT /api/v1/empresas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> updateEmpresa(
        @PathVariable Integer id, 
        @Valid @RequestBody EmpresaUpdateDTO dto) {
        
        EmpresaResponseDTO response = empresaService.update(id, dto);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping 
    public ResponseEntity<List<EmpresaResponseDTO>> getAllEmpresas() {
        
        List<EmpresaResponseDTO> response = empresaService.findAll();
        
        return ResponseEntity.ok(response);
    }
}
