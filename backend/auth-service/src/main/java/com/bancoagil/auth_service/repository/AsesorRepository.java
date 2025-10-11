package com.bancoagil.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.auth_service.model.Asesor;

// Repositorio para la entidad Asesor
@Repository
public interface AsesorRepository extends JpaRepository<Asesor, Long> {
    Optional<Asesor> findByIdUsuario(Long idUsuario); // Buscar asesor por ID de usuario
    Optional<Asesor> findByCodigoEmpleado(String codigoEmpleado); // Buscar asesor por código de empleado
}