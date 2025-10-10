package com.bancoagil.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bancoagil.auth_service.model.Cuenta;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    List<Cuenta> findByIdCliente(Long idCliente);
    
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    boolean existsByNumeroCuenta(String numeroCuenta);
    
    @Query("SELECT COALESCE(MAX(c.id), 0) FROM Cuenta c")
    Long findMaxId();
}