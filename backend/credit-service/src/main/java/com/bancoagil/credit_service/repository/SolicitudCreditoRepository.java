package com.bancoagil.credit_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.credit_service.model.SolicitudCredito;

// Repositorio para gestionar solicitudes de crédito en la base de datos
@Repository
public interface  SolicitudCreditoRepository extends JpaRepository<SolicitudCredito, Long> {
    // Buscar solicitudes por cliente
    List<SolicitudCredito> findByIdCliente(Long idCliente);
    
    // Buscar solicitudes por estado
    List<SolicitudCredito> findByEstado(SolicitudCredito.Estado estado);
    
    // Buscar solicitudes por asesor asignado
    List<SolicitudCredito> findByIdAsesorAsignado(Long idAsesor);
    
    // Contar solicitudes por estado
    long countByEstado(SolicitudCredito.Estado estado);
}
