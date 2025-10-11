package com.bancoagil.credit_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.credit_service.model.PagoCredito;

// Repositorio para la entidad PagoCredito
@Repository
public interface PagoCreditoRepository extends JpaRepository<PagoCredito, Long> {
    
    List<PagoCredito> findByIdCredito(Long idCredito); // Buscar pagos por ID de crédito
    
    List<PagoCredito> findByIdCreditoOrderByNumeroCuotaAsc(Long idCredito); // Buscar pagos por ID de crédito ordenados por número de cuota
}