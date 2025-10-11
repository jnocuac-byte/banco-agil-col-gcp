package com.bancoagil.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.auth_service.model.PersonaNatural;

// Repositorio para la entidad PersonaNatural
@Repository
public interface PersonaNaturalRepository extends JpaRepository<PersonaNatural, Long> {
    Optional<PersonaNatural> findByIdCliente(Long idCliente); // Buscar persona natural por ID de cliente
    boolean existsByNumDocumento(String numDocumento); // Verificar existencia por número de documento
}
