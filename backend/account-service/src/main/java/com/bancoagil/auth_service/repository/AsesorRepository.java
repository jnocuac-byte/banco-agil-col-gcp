package com.bancoagil.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.auth_service.model.Asesor;

@Repository
public interface AsesorRepository extends JpaRepository<Asesor, Long> {
    Optional<Asesor> findByIdUsuario(Long idUsuario);
    Optional<Asesor> findByCodigoEmpleado(String codigoEmpleado);
}