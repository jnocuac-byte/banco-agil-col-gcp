package com.bancoagil.auth_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancoagil.auth_service.dto.ActualizarAsesorDTO;
import com.bancoagil.auth_service.dto.AsesorPerfilDTO;
import com.bancoagil.auth_service.dto.CambiarPasswordDTO;
import com.bancoagil.auth_service.service.AsesorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asesores")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"})
public class AsesorController {

    @Autowired
    private AsesorService asesorService;

    /**
     * GET /api/asesores/{id} - Obtener datos del asesor
     */
    @GetMapping("/{id}")
    public ResponseEntity<AsesorPerfilDTO> obtenerAsesor(@PathVariable Long id) {
        try {
            AsesorPerfilDTO perfil = asesorService.obtenerPerfilAsesor(id);
            return ResponseEntity.ok(perfil);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * PUT /api/asesores/{id} - Actualizar datos del asesor
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarAsesor(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarAsesorDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {
            asesorService.actualizarAsesor(id, dto);

            // Obtener datos actualizados
            AsesorPerfilDTO perfilActualizado = asesorService.obtenerPerfilAsesor(id);

            response.put("success", true);
            response.put("message", "Perfil actualizado exitosamente");
            response.put("asesor", perfilActualizado);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            // Determinar el código de estado apropiado
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
    }

    /**
     * PUT /api/asesores/{id}/cambiar-password - Cambiar contraseña
     */
    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<Map<String, Object>> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {
            asesorService.cambiarPassword(id, dto);

            response.put("success", true);
            response.put("message", "Contraseña actualizada exitosamente");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            // Determinar el código de estado apropiado
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (e.getMessage().contains("incorrecta")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
    }
}