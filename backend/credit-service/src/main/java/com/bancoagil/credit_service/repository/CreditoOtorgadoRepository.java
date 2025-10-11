package com.bancoagil.credit_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.credit_service.model.CreditoOtorgado;

// Repositorio para la entidad CreditoOtorgado
@Repository
public interface CreditoOtorgadoRepository extends JpaRepository<CreditoOtorgado, Long> {
    
    List<CreditoOtorgado> findByIdCliente(Long idCliente); // Buscar créditos por ID de cliente
    
    Optional<CreditoOtorgado> findByIdSolicitud(Long idSolicitud); // Buscar crédito por ID de solicitud
    
    List<CreditoOtorgado> findByEstadoCredito(CreditoOtorgado.EstadoCredito estado); // Buscar créditos por estado
    
    long countByEstadoCredito(CreditoOtorgado.EstadoCredito estado); // Contar créditos por estado
}