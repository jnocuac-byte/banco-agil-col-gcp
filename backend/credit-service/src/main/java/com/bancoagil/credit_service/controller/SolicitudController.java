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

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"})
public class SolicitudController {
    
    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private SolicitudCreditoRepository solicitudRepository; 
    
    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearSolicitud(@Valid @RequestBody SolicitudCreditoDTO dto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            SolicitudCredito solicitud = solicitudService.crearSolicitud(dto);
            
            response.put("success", true);
            response.put("message", "Solicitud creada exitosamente");
            response.put("solicitudId", solicitud.getId());
            response.put("estado", solicitud.getEstado());
            response.put("tasaInteres", solicitud.getTasaInteres());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<SolicitudCredito>> obtenerSolicitudesCliente(@PathVariable Long idCliente) {
        List<SolicitudCredito> solicitudes = solicitudService.obtenerSolicitudesCliente(idCliente);
        return ResponseEntity.ok(solicitudes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudCredito> obtenerSolicitud(@PathVariable Long id) {
        try {
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "credit-service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/todas")
    public ResponseEntity<List<SolicitudCredito>> obtenerTodasSolicitudes() {
        List<SolicitudCredito> solicitudes = solicitudRepository.findAll();
        return ResponseEntity.ok(solicitudes);
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<Map<String, Object>> aprobarSolicitud(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);
            
            solicitud.setEstado(SolicitudCredito.Estado.APROBADA);
            solicitud.setFechaDecision(LocalDateTime.now());
            
            // Asignar asesor si viene en el request
            if (request.containsKey("asesorId")) {
                solicitud.setIdAsesorAsignado(((Number) request.get("asesorId")).longValue());
            }
            
            solicitudRepository.save(solicitud);
            
            response.put("success", true);
            response.put("message", "Solicitud aprobada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<Map<String, Object>> rechazarSolicitud(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);
            
            solicitud.setEstado(SolicitudCredito.Estado.RECHAZADA);
            solicitud.setFechaDecision(LocalDateTime.now());
            
            // Asignar asesor
            if (request.containsKey("asesorId")) {
                solicitud.setIdAsesorAsignado(((Number) request.get("asesorId")).longValue());
            }
            
            // Guardar motivo en observaciones
            if (request.containsKey("motivo")) {
                String motivoRechazo = (String) request.get("motivo");
                String observacionesActuales = solicitud.getObservaciones() != null ? 
                    solicitud.getObservaciones() : "";
                solicitud.setObservaciones(observacionesActuales + 
                    "\n[RECHAZADA] Motivo: " + motivoRechazo);
            }
            
            solicitudRepository.save(solicitud);
            
            response.put("success", true);
            response.put("message", "Solicitud rechazada");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
}
