package com.bancoagil.credit_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.credit_service.model.Documento;

// Repositorio para gestionar documentos en la base de datos
@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    
    // Buscar todos los documentos de una solicitud
    List<Documento> findByIdSolicitud(Long idSolicitud);
    
    // Contar documentos por solicitud
    long countByIdSolicitud(Long idSolicitud);
    
    // Buscar por tipo de documento y solicitud
    List<Documento> findByIdSolicitudAndTipoDocumento(Long idSolicitud, String tipoDocumento);
    
    // Verificar si existe un tipo de documento para una solicitud
    boolean existsByIdSolicitudAndTipoDocumento(Long idSolicitud, String tipoDocumento);
}