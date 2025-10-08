package com.bancoagil.account_service.repository;

import com.bancoagil.account_service.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByIdUsuario(Integer idUsuario);
    boolean existsByIdUsuario(Integer idUsuario);
}
