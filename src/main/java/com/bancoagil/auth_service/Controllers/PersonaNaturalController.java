package com.bancoagil.auth_service.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancoagil.auth_service.Dtos.PersonaNaturalCreateDTO;
import com.bancoagil.auth_service.Dtos.PersonaNaturalResponseDTO;
import com.bancoagil.auth_service.Dtos.PersonaNaturalUpdateDTO;
import com.bancoagil.auth_service.ServiceInterface.IPersonaNaturalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/personas-naturales")
@RequiredArgsConstructor // Inyecta el servicio
public class PersonaNaturalController {

    private final IPersonaNaturalService personaNaturalService;

    // 1. ENDPOINT DE CREACIÓN (POST)
    /**
     * Registra la información de detalle de una Persona Natural y la asocia a un Cliente existente.
     * URL: POST /api/v1/personas-naturales
     */
    @PostMapping
    public ResponseEntity<PersonaNaturalResponseDTO> createPersonaNatural(
        @Valid @RequestBody PersonaNaturalCreateDTO dto) {
        
        PersonaNaturalResponseDTO response = personaNaturalService.create(dto);
        
        // Retornamos 201 Created
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. ENDPOINT DE LECTURA (GET)
    /**
     * Obtiene la información de una Persona Natural por su ID (PK de la tabla persona_natural).
     * URL: GET /api/v1/personas-naturales/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonaNaturalResponseDTO> getPersonaNaturalById(@PathVariable Integer id) {
        
        PersonaNaturalResponseDTO response = personaNaturalService.findById(id);
        
        return ResponseEntity.ok(response);
    }
    
    // 3. ENDPOINT DE ACTUALIZACIÓN (PUT o PATCH)
    /**
     * Actualiza la información de detalle de una Persona Natural.
     * Usamos PUT/PATCH con el ID de la tabla persona_natural.
     * URL: PUT /api/v1/personas-naturales/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonaNaturalResponseDTO> updatePersonaNatural(
        @PathVariable Integer id, 
        @Valid @RequestBody PersonaNaturalUpdateDTO dto) {
        
        PersonaNaturalResponseDTO response = personaNaturalService.update(id, dto);
        
        return ResponseEntity.ok(response);
    }
}
