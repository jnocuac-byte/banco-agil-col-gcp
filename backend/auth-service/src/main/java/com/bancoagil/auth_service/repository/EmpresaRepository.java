package com.bancoagil.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.auth_service.model.Empresa;

// Repositorio para la entidad Empresa
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByIdCliente(Long idCliente); // Buscar empresa por ID de cliente
    boolean existsByNit(String nit); // Verificar existencia de empresa por NIT
}
