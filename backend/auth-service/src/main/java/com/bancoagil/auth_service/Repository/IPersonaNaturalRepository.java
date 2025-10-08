package com.bancoagil.auth_service.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bancoagil.auth_service.Entities.PersonaNatural;

public interface IPersonaNaturalRepository extends JpaRepository<PersonaNatural, Integer> {

    // 🛑 CORRECCIÓN: Usar el nombre de la propiedad de la entidad (identificacion)
    Optional<PersonaNatural> findByIdentificacion(String identificacion); 
    
    // Si necesitas que el nombre sea "numDocumento" en el código, puedes usar:
    // @Query("SELECT p FROM PersonaNatural p WHERE p.identificacion = :numDocumento")
    // Optional<PersonaNatural> findByNumDocumento(@Param("numDocumento") String numDocumento);
}
