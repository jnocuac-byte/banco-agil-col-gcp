package com.bancoagil.credit_service.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.credit_service.dto.SolicitudCreditoDTO;
import com.bancoagil.credit_service.model.SolicitudCredito;
import com.bancoagil.credit_service.repository.SolicitudCreditoRepository;

@Service
public class SolicitudService {
    
    @Autowired
    private SolicitudCreditoRepository solicitudRepository;
    
    @Transactional
    public SolicitudCredito crearSolicitud(SolicitudCreditoDTO dto) {
        SolicitudCredito solicitud = new SolicitudCredito();
        solicitud.setIdCliente(dto.getIdCliente());
        solicitud.setMontoSolicitado(dto.getMontoSolicitado());
        solicitud.setPlazoMeses(dto.getPlazoMeses());
        solicitud.setObservaciones(dto.getObservaciones());
    
        BigDecimal tasaInteres = calcularTasaInteres(dto.getMontoSolicitado(), dto.getPlazoMeses());
        solicitud.setTasaInteres(tasaInteres);

        solicitud.setEstado(SolicitudCredito.Estado.PENDIENTE);
        
        return solicitudRepository.save(solicitud);
    }
    
    @Transactional(readOnly = true)
    public List<SolicitudCredito> obtenerSolicitudesCliente(Long idCliente) {
        return solicitudRepository.findByIdCliente(idCliente);
    }
    
    @Transactional(readOnly = true)
    public SolicitudCredito obtenerSolicitudPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    }
    
    private BigDecimal calcularTasaInteres(BigDecimal monto, @SuppressWarnings("unused") Integer plazo) {
        
        if (monto.compareTo(new BigDecimal("10000000")) < 0) {
            return new BigDecimal("12.0");
        } else if (monto.compareTo(new BigDecimal("50000000")) < 0) {
            return new BigDecimal("10.0");
        } else {
            return new BigDecimal("8.5");
        }
    }
}