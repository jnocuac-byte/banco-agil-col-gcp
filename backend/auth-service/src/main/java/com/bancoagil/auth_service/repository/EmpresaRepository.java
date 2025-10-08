package com.bancoagil.auth_service.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bancoagil.auth_service.model.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByIdCliente(Long idCliente);
    boolean existsByNit(String nit);
}
