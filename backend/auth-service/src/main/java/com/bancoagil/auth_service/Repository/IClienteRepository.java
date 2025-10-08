package com.bancoagil.auth_service.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bancoagil.auth_service.Entities.Cliente;

public interface IClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByUsuarioId(Integer usuarioId);
}
