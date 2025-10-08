package com.bancoagil.auth_service.ServiceInterface;

import java.util.List;

import com.bancoagil.auth_service.Dtos.UsuarioCreateDTO;
import com.bancoagil.auth_service.Dtos.UsuarioResponseDTO;
import com.bancoagil.auth_service.Dtos.UsuarioUpdateDTO;

public interface IUsuarioService {

    /**
     * Crea un nuevo usuario en el sistema.
     * @param usuarioDto Datos de entrada necesarios para la creación.
     * @return El DTO de respuesta del usuario creado.
     */
    UsuarioResponseDTO createUsuario(UsuarioCreateDTO usuarioDto);

    /**
     * Obtiene un usuario por su ID.
     * @param id El ID del usuario.
     * @return El DTO de respuesta del usuario encontrado.
     * @throws com.banca.authservice.exceptions.ResourceNotFoundException Si el usuario no existe.
     */
    UsuarioResponseDTO getUsuarioById(Integer id);

    /**
     * Obtiene todos los usuarios.
     * @return Una lista de DTOs de respuesta de usuarios.
     */
    List<UsuarioResponseDTO> getAllUsuarios();

    /**
     * Actualiza la información básica de un usuario.
     * @param id El ID del usuario a actualizar.
     * @param usuarioDto Datos de actualización opcionales.
     * @return El DTO de respuesta del usuario actualizado.
     * @throws com.banca.authservice.exceptions.ResourceNotFoundException Si el usuario no existe.
     */
    UsuarioResponseDTO updateUsuario(Integer id, UsuarioUpdateDTO usuarioDto);

    /**
     * Elimina un usuario del sistema por su ID.
     * @param id El ID del usuario a eliminar.
     * @throws com.banca.authservice.exceptions.ResourceNotFoundException Si el usuario no existe.
     */
    void deleteUsuario(Integer id);

    /**
     * Método adicional para la autenticación: buscar por email.
     * @param email El email del usuario.
     * @return El DTO de respuesta del usuario encontrado.
     * @throws com.banca.authservice.exceptions.ResourceNotFoundException Si el usuario no existe.
     */
    UsuarioResponseDTO getUsuarioByEmail(String email);
}