package com.bancoagil.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancoagil.auth_service.model.Usuario;

// Repositorio para la entidad Usuario
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email); // Buscar usuario por email
    
    boolean existsByEmail(String email); // Verificar existencia de usuario por email
}
