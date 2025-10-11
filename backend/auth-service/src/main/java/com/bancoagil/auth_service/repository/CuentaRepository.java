package com.bancoagil.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bancoagil.auth_service.model.Cuenta;

// Repositorio para la entidad Cuenta
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    List<Cuenta> findByIdCliente(Long idCliente); // Buscar cuentas por ID de cliente
    
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta); // Buscar cuenta por número de cuenta
    
    boolean existsByNumeroCuenta(String numeroCuenta); // Verificar existencia de cuenta por número de cuenta
    
    @Query("SELECT COALESCE(MAX(c.id), 0) FROM Cuenta c") // Consulta para obtener el máximo ID de cuenta
    Long findMaxId(); // Método para obtener el máximo ID de cuenta
}