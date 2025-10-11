package com.bancoagil.auth_service.controller;

import java.util.HashMap;
import java.util.List;
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

import com.bancoagil.auth_service.dto.AsesorDTO;
import com.bancoagil.auth_service.dto.ClienteDTO;
import com.bancoagil.auth_service.dto.ClienteDetalleDTO;
import com.bancoagil.auth_service.dto.EstadoDTO;
import com.bancoagil.auth_service.dto.EstadisticasAsesorDTO;
import com.bancoagil.auth_service.dto.SolicitudDTO;
import com.bancoagil.auth_service.service.UsuarioService;

// Controlador REST para gestionar usuarios (clientes y asesores)
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"})
public class UsuarioController {

    // Inyección del servicio de usuarios
    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para listar todos los clientes
    @GetMapping("/clientes")
    // Listar todos los clientes
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        // Intentar obtener la lista de clientes
        try {
            List<ClienteDTO> clientes = usuarioService.listarClientes(); // Obtener lista de clientes
            return ResponseEntity.ok(clientes); // Devolver lista con estado 200 OK
        } catch (Exception e) { // Manejar errores
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Devolver 500 en caso de error
        }
    }

    // Endpoint para obtener detalles de un cliente por ID
    @GetMapping("/clientes/{id}")
    // Obtener detalles de un cliente por ID
    public ResponseEntity<ClienteDetalleDTO> obtenerCliente(@PathVariable Long id) {
        // Intentar obtener los detalles del cliente
        try {
            ClienteDetalleDTO cliente = usuarioService.obtenerClienteDetalle(id); // Obtener detalles del cliente
            return ResponseEntity.ok(cliente); // Devolver detalles con estado 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Devolver 404 si no se encuentra el cliente
        }
    }

    // Endpoint para obtener solicitudes de un cliente por ID
    @GetMapping("/clientes/{id}/solicitudes")
    // Obtener solicitudes de un cliente por ID
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudesCliente(@PathVariable Long id) {
        // Intentar obtener las solicitudes del cliente
        try {
            List<SolicitudDTO> solicitudes = usuarioService.obtenerSolicitudesCliente(id); // Obtener solicitudes del cliente
            return ResponseEntity.ok(solicitudes); // Devolver solicitudes con estado 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Devolver 500 en caso de error
        }
    }

    // Endpoint para cambiar el estado (activo/inactivo) de un cliente
    @PutMapping("/clientes/{id}/estado")
    // Cambiar el estado (activo/inactivo) de un cliente
    public ResponseEntity<Map<String, Object>> cambiarEstadoCliente(
            @PathVariable Long id,
            @RequestBody EstadoDTO dto) {
        
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar cambiar el estado del cliente
        try {
            // Llamar al servicio para cambiar el estado del cliente
            usuarioService.cambiarEstadoCliente(id, dto.getActivo());
            // Preparar respuesta exitosa
            response.put("success", true);
            response.put("message", "Estado actualizado correctamente");
            // Devolver respuesta con estado 200 OK
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) { // Manejar errores
            // Preparar respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());
            // Devolver respuesta con estado 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Endpoint para listar todos los asesores
    @GetMapping("/asesores")
    // Listar todos los asesores
    public ResponseEntity<List<AsesorDTO>> listarAsesores() {
        // Intentar obtener la lista de asesores
        try {
            List<AsesorDTO> asesores = usuarioService.listarAsesores(); // Obtener lista de asesores
            return ResponseEntity.ok(asesores); // Devolver lista con estado 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Devolver 500 en caso de error
        }
    }

    // Endpoint para obtener un asesor por ID
    @GetMapping("/asesores/{id}")
    // Obtener un asesor por ID
    public ResponseEntity<AsesorDTO> obtenerAsesor(@PathVariable Long id) {
        // Intentar obtener el asesor
        try {
            AsesorDTO asesor = usuarioService.obtenerAsesor(id); // Obtener asesor por ID
            return ResponseEntity.ok(asesor); // Devolver asesor con estado 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Devolver 404 en caso de no encontrar
        }
    }

    // Endpoint para obtener estadísticas de un asesor por ID
    @GetMapping("/asesores/{id}/estadisticas")
    // Obtener estadísticas de un asesor por ID
    public ResponseEntity<EstadisticasAsesorDTO> obtenerEstadisticasAsesor(@PathVariable Long id) {
        // Intentar obtener las estadísticas del asesor
        try {
            EstadisticasAsesorDTO stats = usuarioService.obtenerEstadisticasAsesor(id); // Obtener estadísticas del asesor
            return ResponseEntity.ok(stats); // Devolver estadísticas con estado 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Devolver 500 en caso de error
        }
    }

    // Endpoint para cambiar el estado (activo/inactivo) de un asesor
    @PutMapping("/asesores/{id}/estado")
    // Cambiar el estado (activo/inactivo) de un asesor
    public ResponseEntity<Map<String, Object>> cambiarEstadoAsesor(
            @PathVariable Long id,
            @RequestBody EstadoDTO dto) {
        
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar cambiar el estado del asesor
        try {
            // Llamar al servicio para cambiar el estado del asesor
            usuarioService.cambiarEstadoAsesor(id, dto.getActivo());
            // Preparar respuesta exitosa
            response.put("success", true);
            response.put("message", "Estado actualizado correctamente");
            // Devolver respuesta con estado 200 OK
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) { // Manejar errores
            // Preparar respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());
            // Devolver respuesta con estado 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}