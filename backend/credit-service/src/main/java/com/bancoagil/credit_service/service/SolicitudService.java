package com.bancoagil.credit_service.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.credit_service.dto.SolicitudCreditoDTO;
import com.bancoagil.credit_service.model.SolicitudCredito;
import com.bancoagil.credit_service.repository.SolicitudCreditoRepository;

// Servicio para gestionar la lógica de negocio de solicitudes de crédito
@Service
public class SolicitudService {
    
    // Inyección del repositorio de solicitudes de crédito
    @Autowired
    private SolicitudCreditoRepository solicitudRepository;
    
    // Crear una nueva solicitud de crédito
    @Transactional
    public SolicitudCredito crearSolicitud(SolicitudCreditoDTO dto) {
        // Mapear DTO a entidad
        SolicitudCredito solicitud = new SolicitudCredito();
        solicitud.setIdCliente(dto.getIdCliente());
        solicitud.setMontoSolicitado(dto.getMontoSolicitado());
        solicitud.setPlazoMeses(dto.getPlazoMeses());
        solicitud.setObservaciones(dto.getObservaciones());
        
        // Calcular tasa de interés basada en monto y plazo
        BigDecimal tasaInteres = calcularTasaInteres(dto.getMontoSolicitado(), dto.getPlazoMeses());
        solicitud.setTasaInteres(tasaInteres);

        // Establecer estado inicial como PENDIENTE
        solicitud.setEstado(SolicitudCredito.Estado.PENDIENTE);
        
        // Guardar la solicitud en la base de datos
        return solicitudRepository.save(solicitud);
    }
    
    // Obtener todas las solicitudes de crédito
    @Transactional(readOnly = true)
    public List<SolicitudCredito> obtenerSolicitudesCliente(Long idCliente) {
        return solicitudRepository.findByIdCliente(idCliente); // Buscar solicitudes por cliente
    }
    
    // Obtener una solicitud por su ID
    @Transactional(readOnly = true)
    public SolicitudCredito obtenerSolicitudPorId(Long id) {
        // Buscar solicitud por ID o lanzar error si no se encuentra
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada")); // Lanzar error si no se encuentra
    }
    
    // Método para calcular la tasa de interés basada en monto y plazo
    private BigDecimal calcularTasaInteres(BigDecimal monto, @SuppressWarnings("unused") Integer plazo) {
        // Ejemplo simple: tasas fijas basadas en rangos de monto
        if (monto.compareTo(new BigDecimal("10000000")) < 0) {
            return new BigDecimal("12.0"); // 12% para montos menores a 10 millones
        } else if (monto.compareTo(new BigDecimal("50000000")) < 0) {
            return new BigDecimal("10.0"); // 10% para montos entre 10 y 50 millones
        } else {
            return new BigDecimal("8.5"); // 8.5% para montos mayores a 50 millones
        }
    }
}