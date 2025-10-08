package com.bancoagil.account_service.repository;

import com.bancoagil.account_service.model.PersonaNatural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaNaturalRepository extends JpaRepository<PersonaNatural, Long> {
    Optional<PersonaNatural> findByIdCliente(Long idCliente);
    Optional<PersonaNatural> findByNumDocumento(String numDocumento);
    boolean existsByNumDocumento(String numDocumento);
    boolean existsByIdCliente(Long idCliente);
}
