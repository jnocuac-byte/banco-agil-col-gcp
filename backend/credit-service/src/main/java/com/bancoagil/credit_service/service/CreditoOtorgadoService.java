package com.bancoagil.credit_service.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bancoagil.credit_service.dto.TransaccionDTO;
import com.bancoagil.credit_service.model.CreditoOtorgado;
import com.bancoagil.credit_service.model.SolicitudCredito;
import com.bancoagil.credit_service.repository.CreditoOtorgadoRepository;
import com.bancoagil.credit_service.repository.SolicitudCreditoRepository;

@Service
public class CreditoOtorgadoService {
    
    @Autowired
    private CreditoOtorgadoRepository creditoRepository;
    
    @Autowired
    private SolicitudCreditoRepository solicitudRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    // URL del microservicio de cuentas (ajustar según tu configuración)
    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8083/api/cuentas";
    
    /**
     * Aprobar solicitud y crear crédito otorgado
     */
    @Transactional
    public CreditoOtorgado aprobarYDesembolsar(Long solicitudId, Long asesorId, Long cuentaDesembolsoId) {
        // 1. Obtener y validar solicitud
        SolicitudCredito solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        if (solicitud.getEstado() != SolicitudCredito.Estado.PENDIENTE 
            && solicitud.getEstado() != SolicitudCredito.Estado.EN_REVISION) {
            throw new RuntimeException("La solicitud ya fue procesada");
        }
        
        // 2. Actualizar solicitud a APROBADA
        solicitud.setEstado(SolicitudCredito.Estado.APROBADA);
        solicitud.setFechaDecision(LocalDateTime.now());
        solicitud.setIdAsesorAsignado(asesorId);
        solicitudRepository.save(solicitud);
        
        // 3. Calcular cuota mensual
        BigDecimal cuotaMensual = calcularCuotaMensual(
            solicitud.getMontoSolicitado(),
            solicitud.getPlazoMeses(),
            solicitud.getTasaInteres()
        );
        
        // 4. Crear crédito otorgado
        CreditoOtorgado credito = new CreditoOtorgado();
        credito.setIdSolicitud(solicitudId);
        credito.setIdCliente(solicitud.getIdCliente());
        credito.setIdCuentaDesembolso(cuentaDesembolsoId);
        
        credito.setMontoOriginal(solicitud.getMontoSolicitado());
        credito.setPlazoMeses(solicitud.getPlazoMeses());
        credito.setTasaInteres(solicitud.getTasaInteres());
        credito.setCuotaMensual(cuotaMensual);
        
        credito.setSaldoPendiente(solicitud.getMontoSolicitado());
        credito.setCuotasPagadas(0);
        credito.setCuotasPendientes(solicitud.getPlazoMeses());
        
        LocalDate hoy = LocalDate.now();
        credito.setProximaFechaPago(hoy.plusMonths(1));
        credito.setFechaVencimiento(hoy.plusMonths(solicitud.getPlazoMeses()));
        credito.setEstadoCredito(CreditoOtorgado.EstadoCredito.ACTIVO);
        
        CreditoOtorgado creditoGuardado = creditoRepository.save(credito);
        
        // 5. Registrar transacción de desembolso (llamar al microservicio de cuentas)
        try {
            registrarDesembolso(cuentaDesembolsoId, solicitud.getMontoSolicitado(), solicitudId);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar desembolso: " + e.getMessage());
        }
        
        return creditoGuardado;
    }
    
    /**
     * Calcular cuota mensual usando fórmula de amortización francesa
     */
    private BigDecimal calcularCuotaMensual(BigDecimal montoOriginal, Integer plazoMeses, BigDecimal tasaAnual) {
        // Convertir tasa anual a mensual decimal
        BigDecimal tasaMensual = tasaAnual.divide(
            new BigDecimal("1200"), 
            10, 
            RoundingMode.HALF_UP
        );
        
        // Si la tasa es 0, cuota = monto / plazo
        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0) {
            return montoOriginal.divide(
                new BigDecimal(plazoMeses), 
                2, 
                RoundingMode.HALF_UP
            );
        }
        
        // Fórmula: C = P * [i(1+i)^n] / [(1+i)^n - 1]
        BigDecimal unoPlusTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal potencia = unoPlusTasa.pow(plazoMeses, new MathContext(10));
        
        BigDecimal numerador = montoOriginal.multiply(tasaMensual).multiply(potencia);
        BigDecimal denominador = potencia.subtract(BigDecimal.ONE);
        
        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Registrar desembolso en el microservicio de cuentas
     */
    private void registrarDesembolso(Long cuentaId, BigDecimal monto, Long solicitudId) {
        String url = ACCOUNT_SERVICE_URL + "/" + cuentaId + "/desembolso";
        
        TransaccionDTO transaccion = new TransaccionDTO();
        transaccion.setMonto(monto);
        transaccion.setDescripcion("Desembolso de crédito - Solicitud #" + solicitudId);
        
        restTemplate.postForEntity(url, transaccion, Void.class);
    }
    
    /**
     * Obtener todos los créditos de un cliente
     */
    @Transactional(readOnly = true)
    public List<CreditoOtorgado> obtenerCreditosCliente(Long idCliente) {
        return creditoRepository.findByIdCliente(idCliente);
    }
    
    /**
     * Obtener crédito por ID
     */
    @Transactional(readOnly = true)
    public CreditoOtorgado obtenerCreditoPorId(Long id) {
        return creditoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Crédito no encontrado"));
    }
}