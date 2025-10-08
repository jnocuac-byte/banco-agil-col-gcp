package com.bancoagil.auth_service.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bancoagil.auth_service.Entities.Usuario;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Método crucial para la autenticación: Buscar un usuario por su email.
     * Spring Data JPA infiere la consulta SQL a partir del nombre del método.
     *
     * @param email El email del usuario a buscar.
     * @return Un Optional que puede contener el Usuario si es encontrado.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Opcional: Verificar si un email ya existe en la base de datos.
     * Útil para la validación al crear o actualizar un usuario.
     *
     * @param email El email a verificar.
     * @return true si el email ya está registrado, false en caso contrario.
     */
    boolean existsByEmail(String email);
}