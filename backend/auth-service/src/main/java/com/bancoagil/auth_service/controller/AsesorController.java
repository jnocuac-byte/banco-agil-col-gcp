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

// Controlador REST para gestionar asesores
@RestController
@RequestMapping("/api/asesores")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"})
public class AsesorController {

    // Servicio para manejar la lógica de negocio relacionada con asesores
    @Autowired
    private AsesorService asesorService;

    /**
     * GET /api/asesores/{id} - Obtener perfil del asesor por ID
     */
    @GetMapping("/{id}")
    // Obtener perfil del asesor por ID
    public ResponseEntity<AsesorPerfilDTO> obtenerAsesor(@PathVariable Long id) {
        // Llamar al servicio para obtener el perfil del asesor
        try {
            AsesorPerfilDTO perfil = asesorService.obtenerPerfilAsesor(id); // Obtener perfil del asesor
            return ResponseEntity.ok(perfil); // Devolver perfil con estado 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Devolver 404 si no se encuentra el asesor
        }
    }

    /**
     * PUT /api/asesores/{id} - Actualizar datos del asesor
     */
    @PutMapping("/{id}")
    // Actualizar datos del asesor
    public ResponseEntity<Map<String, Object>> actualizarAsesor(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarAsesorDTO dto) { // Datos para actualizar
        
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();

        // Intentar actualizar el asesor
        try {
            // Llamar al servicio para actualizar el asesor
            asesorService.actualizarAsesor(id, dto);

            // Obtener datos actualizados
            AsesorPerfilDTO perfilActualizado = asesorService.obtenerPerfilAsesor(id);

            // Preparar respuesta exitosa
            response.put("success", true);
            response.put("message", "Perfil actualizado exitosamente");
            response.put("asesor", perfilActualizado);

            // Devolver respuesta con estado 200 OK
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {// Manejar errores
            // Preparar respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());

            // Determinar el código de estado apropiado
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 si no se encuentra el asesor
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 para otros errores
            }
        }
    }

    /**
     * PUT /api/asesores/{id}/cambiar-password - Cambiar contraseña
     */
    @PutMapping("/{id}/cambiar-password")
    // Cambiar contraseña del asesor
    public ResponseEntity<Map<String, Object>> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordDTO dto) {

        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();

        // Intentar cambiar la contraseña
        try {
            // Llamar al servicio para cambiar la contraseña
            asesorService.cambiarPassword(id, dto);

            // Preparar respuesta exitosa
            response.put("success", true);
            response.put("message", "Contraseña actualizada exitosamente");

            // Devolver respuesta con estado 200 OK
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) { // Manejar errores
            // Preparar respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());

            // Determinar el código de estado apropiado
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 si no se encuentra el asesor
            } else if (e.getMessage().contains("incorrecta")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 si la contraseña actual es incorrecta
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 para otros errores
            }
        }
    }
}