package com.bancoagil.credit_service.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancoagil.credit_service.dto.SolicitudCreditoDTO;
import com.bancoagil.credit_service.model.SolicitudCredito;
import com.bancoagil.credit_service.repository.SolicitudCreditoRepository;
import com.bancoagil.credit_service.service.SolicitudService;

import jakarta.validation.Valid;

// Controlador para gestionar solicitudes de crédito
@RestController
@RequestMapping("/api/solicitudes")
// @CrossOrigin(origins = "http://localhost:5500") // Permitir solicitudes desde el frontend
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"})
public class SolicitudController {
    
    // Inyección del servicio de solicitudes
    @Autowired
    private SolicitudService solicitudService;

    // Inyección del repositorio de solicitudes (para obtener todas las solicitudes)
    @Autowired
    private SolicitudCreditoRepository solicitudRepository; 
    
    // Endpoint para crear una nueva solicitud de crédito
    @PostMapping("/crear")
    // Crear nueva solicitud de crédito
    public ResponseEntity<Map<String, Object>> crearSolicitud(@Valid @RequestBody SolicitudCreditoDTO dto) {
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar crear la solicitud
        try {
            // Llamar al servicio para crear la solicitud
            SolicitudCredito solicitud = solicitudService.crearSolicitud(dto);
            
            // Preparar la respuesta exitosa
            response.put("success", true);
            response.put("message", "Solicitud creada exitosamente");
            response.put("solicitudId", solicitud.getId());
            response.put("estado", solicitud.getEstado());
            response.put("tasaInteres", solicitud.getTasaInteres());
            
            // Retornar la respuesta con estado 201 (CREATED)
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) { // Manejar errores del servicio
            // Preparar la respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());

            // Retornar la respuesta con estado 400 (BAD REQUEST)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Endpoint para obtener todas las solicitudes de un cliente
    @GetMapping("/cliente/{idCliente}")
    // Obtener solicitudes de un cliente por su ID
    public ResponseEntity<List<SolicitudCredito>> obtenerSolicitudesCliente(@PathVariable Long idCliente) {
        // Llamar al servicio para obtener las solicitudes del cliente
        List<SolicitudCredito> solicitudes = solicitudService.obtenerSolicitudesCliente(idCliente);

        // Retornar respuesta con la lista de solicitudes con estado 200 (OK)
        return ResponseEntity.ok(solicitudes);
    }
    
    // Endpoint para obtener una solicitud por su ID
    @GetMapping("/{id}")
    // Obtener solicitud por ID
    public ResponseEntity<SolicitudCredito> obtenerSolicitud(@PathVariable Long id) {
        // Intentar obtener la solicitud
        try {
            // Llamar al servicio para obtener la solicitud
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);

            // Retornar la solicitud con estado 200 (OK)
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) { // Manejar error si no se encuentra la solicitud
             // Retornar estado 404 (NOT FOUND)
            return ResponseEntity.notFound().build();
        }
    }
    
    // Endpoint para health check
    @GetMapping("/health")
    // Health check
    public ResponseEntity<Map<String, String>> health() {
        // Respuesta simple para health check
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "credit-service");

        // Retornar la respuesta con estado 200 (OK)
        return ResponseEntity.ok(response);
    }

    // Endpoint para obtener todas las solicitudes (para backoffice)
    @GetMapping("/todas")
    // Obtener todas las solicitudes de crédito
    public ResponseEntity<List<SolicitudCredito>> obtenerTodasSolicitudes() {
        // Llamar al repositorio para obtener todas las solicitudes
        List<SolicitudCredito> solicitudes = solicitudRepository.findAll();

        // Retornar la lista de solicitudes con estado 200 (OK)
        return ResponseEntity.ok(solicitudes);
    }

    // Endpoint para aprobar una solicitud de crédito
    @PutMapping("/{id}/aprobar")
    // Aprobar solicitud por ID
    public ResponseEntity<Map<String, Object>> aprobarSolicitud(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) { // request puede contener "asesorId"
        
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar aprobar la solicitud
        try {
            // Llamar al servicio para obtener la solicitud
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);
            
            // Actualizar estado y fecha de decisión
            solicitud.setEstado(SolicitudCredito.Estado.APROBADA);
            solicitud.setFechaDecision(LocalDateTime.now());
            
            // Asignar asesor si viene en el request
            if (request.containsKey("asesorId")) {
                solicitud.setIdAsesorAsignado(((Number) request.get("asesorId")).longValue()); // Convertir a Long
            }
            
            // Guardar cambios en la base de datos
            solicitudRepository.save(solicitud);
            
            // Preparar la respuesta exitosa
            response.put("success", true);
            response.put("message", "Solicitud aprobada exitosamente");
            
            // Retornar la respuesta con estado 200 (OK)
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) { // Manejar errores (como solicitud no encontrada)
            // Preparar la respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());

            // Retornar la respuesta con estado 400 (BAD REQUEST)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Endpoint para rechazar una solicitud de crédito
    @PutMapping("/{id}/rechazar")
    // Rechazar solicitud por ID
    public ResponseEntity<Map<String, Object>> rechazarSolicitud(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) { // request puede contener "asesorId" y "motivo"
        
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar rechazar la solicitud
        try {
            // Llamar al servicio para obtener la solicitud
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);
            
            // Actualizar estado y fecha de decisión
            solicitud.setEstado(SolicitudCredito.Estado.RECHAZADA);
            solicitud.setFechaDecision(LocalDateTime.now());
            
            // Asignar asesor
            if (request.containsKey("asesorId")) {
                solicitud.setIdAsesorAsignado(((Number) request.get("asesorId")).longValue()); // Convertir a Long
            }
            
            // Guardar motivo en observaciones
            if (request.containsKey("motivo")) {
                // Parsear y agregar motivo al campo de observaciones
                String motivoRechazo = (String) request.get("motivo"); // Obtener motivo del request
                String observacionesActuales = solicitud.getObservaciones() != null ?  // Mantener observaciones previas
                    solicitud.getObservaciones() : ""; // Si es null, usar cadena vacía
                solicitud.setObservaciones(observacionesActuales + 
                    "\n[RECHAZADA] Motivo: " + motivoRechazo); // Agregar nuevo motivo
            }

            // Guardar cambios en la base de datos
            solicitudRepository.save(solicitud);
            
            // Preparar la respuesta exitosa
            response.put("success", true);
            response.put("message", "Solicitud rechazada");
            
            // Retornar la respuesta con estado 200 (OK)
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) { // Manejar errores (como solicitud no encontrada)
            // Preparar la respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());

            // Retornar la respuesta con estado 400 (BAD REQUEST)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Endpoint para marcar solicitud como EN_REVISION
    @PutMapping("/{id}/revision")
    public ResponseEntity<Map<String, Object>> marcarEnRevision(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);
            
            // Cambiar estado a EN_REVISION
            solicitud.setEstado(SolicitudCredito.Estado.EN_REVISION);
            
            // Asignar asesor que revisa
            if (request.containsKey("asesorId")) {
                solicitud.setIdAsesorAsignado(((Number) request.get("asesorId")).longValue());
            }
            
            solicitudRepository.save(solicitud);
            
            response.put("success", true);
            response.put("message", "Solicitud en revisión");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
}
