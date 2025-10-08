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

import com.bancoagil.auth_service.Dtos.UsuarioCreateDTO;
import com.bancoagil.auth_service.Dtos.UsuarioResponseDTO;
import com.bancoagil.auth_service.Dtos.UsuarioUpdateDTO;
import com.bancoagil.auth_service.ServiceInterface.IUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de usuarios (CRUD).
 */
@RestController // Marca la clase como un controlador REST que devuelve JSON/XML
@RequestMapping("/api/v1/usuarios") // Ruta base para todos los endpoints de este controlador
@RequiredArgsConstructor // Inyección por constructor del servicio
public class UsuarioController {

    private final IUsuarioService usuarioService;

    // --- 1. CREAR USUARIO (POST) ---
    // URL: POST /api/v1/usuarios
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(
            @Valid @RequestBody UsuarioCreateDTO usuarioDto) {
        
        UsuarioResponseDTO newUsuario = usuarioService.createUsuario(usuarioDto);
        // Retorna 201 Created
        return new ResponseEntity<>(newUsuario, HttpStatus.CREATED);
    }

    // --- 2. OBTENER TODOS LOS USUARIOS (GET) ---
    // URL: GET /api/v1/usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios); // Retorna 200 OK
    }

    // --- 3. OBTENER USUARIO POR ID (GET) ---
    // URL: GET /api/v1/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Integer id) {
        UsuarioResponseDTO usuario = usuarioService.getUsuarioById(id);
        return ResponseEntity.ok(usuario); // Retorna 200 OK
    }
    
    // --- 4. ACTUALIZAR USUARIO (PUT) ---
    // URL: PUT /api/v1/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioUpdateDTO usuarioDto) {
        
        UsuarioResponseDTO updatedUsuario = usuarioService.updateUsuario(id, usuarioDto);
        return ResponseEntity.ok(updatedUsuario); // Retorna 200 OK
    }
    
    // --- 5. ELIMINAR USUARIO (DELETE) ---
    // URL: DELETE /api/v1/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}