package com.bancoagil.credit_service.controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
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
import org.springframework.web.client.RestTemplate;

import com.bancoagil.credit_service.dto.SolicitudCreditoDTO;
import com.bancoagil.credit_service.model.CreditoOtorgado;
import com.bancoagil.credit_service.model.SolicitudCredito;
import com.bancoagil.credit_service.repository.CreditoOtorgadoRepository;
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
    
    // @Autowired
    // private CreditoOtorgadoService creditoOtorgadoService;

    @Autowired
    private CreditoOtorgadoRepository creditoOtorgadoRepository;    

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
    public ResponseEntity<Map<String, Object>> aprobarSolicitud(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar aprobar la solicitud
        try {
            // Obtener parámetros del request (solo asesorId)
            Long asesorId = request.containsKey("asesorId") ?  // Convertir a Long
                ((Number) request.get("asesorId")).longValue() : null; // Convertir a Long
            
            // Validar que asesorId esté presente
            if (asesorId == null) {
                throw new RuntimeException("Se requiere asesorId"); // Lanzar excepción si falta asesorId
            }
            
            // 1. Obtener la solicitud
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);
            
            // 2. Obtener cuenta de ahorros del cliente desde account-service
            String accountServiceUrl = "http://localhost:8082/api/cuentas/cliente/" + solicitud.getIdCliente() + "/ahorros";
            
            // Llamada REST a account-service
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> cuentaResponse; // Respuesta de account-service
            
            // Manejar posibles errores en la llamada REST
            try {
                cuentaResponse = restTemplate.getForEntity(accountServiceUrl, Map.class); // Hacer la llamada GET
            } catch (org.springframework.web.client.HttpClientErrorException e) { // Manejar errores HTTP
                throw new RuntimeException("Error HTTP al obtener cuenta del cliente: " + e.getMessage()); // Lanzar excepción con mensaje
            } catch (org.springframework.web.client.RestClientException e) { // Manejar otros errores de cliente REST
                throw new RuntimeException("Error de cliente REST al obtener cuenta del cliente: " + e.getMessage()); // Lanzar excepción con mensaje
            }
            
            // Validar respuesta y extraer ID de cuenta
            @SuppressWarnings("unchecked") // Suprimir advertencia de conversión
            Map<String, Object> cuentaBody = (Map<String, Object>) cuentaResponse.getBody(); // Cuerpo de la respuesta
            // Validar que la cuenta exista y tenga ID
            if (cuentaBody == null || !cuentaBody.containsKey("id")) {
                throw new RuntimeException("El cliente no tiene una cuenta de ahorros activa"); // Lanzar excepción si no tiene cuenta
            }
            
            // Extraer ID de cuenta y convertir a Long
            Long cuentaId = ((Number) cuentaBody.get("id")).longValue();
            
            // Actualizar estado a APROBADA
            solicitud.setEstado(SolicitudCredito.Estado.APROBADA);
            solicitud.setFechaDecision(LocalDateTime.now());
            solicitud.setIdAsesorAsignado(asesorId);
            solicitudRepository.save(solicitud);
            
            // Llamar a account-service para registrar desembolso
            String desembolsoUrl = "http://localhost:8082/api/cuentas/" + cuentaId + "/desembolso";
            
            // Preparar datos para el desembolso
            Map<String, Object> desembolsoRequest = new HashMap<>();
            desembolsoRequest.put("idCuenta", cuentaId);
            desembolsoRequest.put("monto", solicitud.getMontoSolicitado());
            desembolsoRequest.put("descripcion", "Desembolso de crédito - Solicitud #" + id); // Descripción del desembolso
            desembolsoRequest.put("idSolicitud", id);
            
            // Hacer la llamada POST para registrar el desembolso
            ResponseEntity<Map> desembolsoResponse = restTemplate.postForEntity(
                desembolsoUrl, 
                desembolsoRequest, 
                Map.class
            );
            
            // Validar respuesta del desembolso
            @SuppressWarnings("unchecked") // Suprimir advertencia de conversión
            Map<String, Object> desembolsoBody = (Map<String, Object>) desembolsoResponse.getBody(); // Cuerpo de la respuesta
            // Validar que el desembolso fue exitoso
            if (desembolsoBody == null || desembolsoBody.get("success") == null ||  // Verificar que success no sea null
                !(Boolean) desembolsoBody.get("success")) { // Verificar éxito
                throw new RuntimeException("Error al registrar desembolso en account-service"); // Lanzar excepción si hubo error
            }
            
            // Calcular cuota mensual
            BigDecimal cuotaMensual = calcularCuotaMensual(
                solicitud.getMontoSolicitado(), 
                solicitud.getPlazoMeses(),
                solicitud.getTasaInteres()
            );
            
            // Crear registro en creditos_otorgados
            CreditoOtorgado credito = new CreditoOtorgado();
            credito.setIdSolicitud(id);
            credito.setIdCliente(solicitud.getIdCliente());
            credito.setIdCuentaDesembolso(cuentaId);
            credito.setMontoOriginal(solicitud.getMontoSolicitado());
            credito.setPlazoMeses(solicitud.getPlazoMeses());
            credito.setTasaInteres(solicitud.getTasaInteres());
            credito.setCuotaMensual(cuotaMensual);
            credito.setSaldoPendiente(solicitud.getMontoSolicitado());
            credito.setCuotasPagadas(0);
            credito.setCuotasPendientes(solicitud.getPlazoMeses());
            
            // Fechas importantes
            LocalDate hoy = LocalDate.now();
            credito.setProximaFechaPago(hoy.plusMonths(1));
            credito.setFechaVencimiento(hoy.plusMonths(solicitud.getPlazoMeses())); // Fecha de vencimiento
            credito.setEstadoCredito(CreditoOtorgado.EstadoCredito.ACTIVO); // Estado inicial
            
            // Guardar en la base de datos
            creditoOtorgadoRepository.save(credito);
            
            // Respuesta exitosa
            response.put("success", true);
            response.put("message", "Solicitud aprobada y crédito desembolsado exitosamente");
            response.put("creditoId", credito.getId());
            response.put("cuotaMensual", cuotaMensual);
            response.put("cuentaId", cuentaId);
            response.put("nuevoSaldo", desembolsoBody.get("nuevoSaldo"));
            
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
        
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar marcar la solicitud como EN_REVISION
        try {
            // Llamar al servicio para obtener la solicitud
            SolicitudCredito solicitud = solicitudService.obtenerSolicitudPorId(id);
            
            // Cambiar estado a EN_REVISION
            solicitud.setEstado(SolicitudCredito.Estado.EN_REVISION);
            
            // Asignar asesor que revisa
            if (request.containsKey("asesorId")) {
                solicitud.setIdAsesorAsignado(((Number) request.get("asesorId")).longValue()); // Convertir a Long
            }
            
            // Guardar cambios en la base de datos
            solicitudRepository.save(solicitud);
            
            // Preparar la respuesta exitosa
            response.put("success", true);
            response.put("message", "Solicitud en revisión");
            
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

    // Método auxiliar para calcular cuota mensual
    private BigDecimal calcularCuotaMensual(BigDecimal monto, Integer plazo, BigDecimal tasaAnual) {
        BigDecimal tasaMensual = tasaAnual.divide( // Convertir tasa anual a mensual
            new BigDecimal("1200"),  // 12 meses * 100 (porcentaje)
            10,  
            RoundingMode.HALF_UP // Redondeo a 10 decimales
        );
        
        // Fórmula de cuota fija: M = P * (r(1+r)^n) / ((1+r)^n - 1)
        // Donde: M = cuota mensual, P = monto, r = tasa mensual, n = plazo en meses
        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0) {
            return monto.divide(
                new BigDecimal(plazo),  // Dividir monto entre plazo
                2,  // Redondeo a 2 decimales
                RoundingMode.HALF_UP // Redondeo
            );
        }
        
        // Calcular (1 + r)^n
        BigDecimal unoPlusTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal potencia = unoPlusTasa.pow(plazo, new MathContext(10));
        
        // Calcular cuota mensual
        BigDecimal numerador = monto.multiply(tasaMensual).multiply(potencia);
        BigDecimal denominador = potencia.subtract(BigDecimal.ONE);
        
        // Dividir y redondear a 2 decimales
        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }
    
}
