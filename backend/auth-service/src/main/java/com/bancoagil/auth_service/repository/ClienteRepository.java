package com.bancoagil.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.auth_service.model.Cliente;

// Repositorio para la entidad Cliente
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
        Optional<Cliente> findByIdUsuario(Long idUsuario); // Buscar cliente por ID de usuario
}
