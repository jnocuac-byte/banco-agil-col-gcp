package com.bancoagil.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bancoagil.auth_service.model.PersonaNatural;

@Repository
public interface PersonaNaturalRepository extends JpaRepository<PersonaNatural, Long> {
    Optional<PersonaNatural> findByIdCliente(Long idCliente);
    boolean existsByNumDocumento(String numDocumento);
}
