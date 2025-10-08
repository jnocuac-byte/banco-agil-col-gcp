package com.bancoagil.account_service.repository;

import com.bancoagil.account_service.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByIdCliente(Long idCliente);
    Optional<Empresa> findByNit(String nit);
    boolean existsByNit(String nit);
    boolean existsByIdCliente(Long idCliente);
}
