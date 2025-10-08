package com.bancoagil.auth_service.Services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.auth_service.Dtos.UsuarioCreateDTO;
import com.bancoagil.auth_service.Dtos.UsuarioResponseDTO;
import com.bancoagil.auth_service.Dtos.UsuarioUpdateDTO;
import com.bancoagil.auth_service.Entities.Usuario;
import com.bancoagil.auth_service.Exceptions.DuplicateResourceException;
import com.bancoagil.auth_service.Exceptions.ResourceNotFoundException;
import com.bancoagil.auth_service.Mappers.IUsuarioMapper;
import com.bancoagil.auth_service.Repository.IUsuarioRepository;
import com.bancoagil.auth_service.ServiceInterface.IUsuarioService;

import lombok.RequiredArgsConstructor;


/**
 * Implementación de la interfaz IUsuarioService.
 * Contiene la lógica de negocio para la gestión de usuarios.
 */
@Service // Marca la clase como un Service (Bean de Spring)
@RequiredArgsConstructor // Genera un constructor con todos los campos 'final' inyectables (Inyección por Constructor)
@Transactional // Asegura que los métodos que modifican el estado de la BD sean transaccionales
public class UsuarioServiceImpl implements IUsuarioService {

    // Inyecciones de dependencias
    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    // --- Lógica de Creación (CREATE) ---

    @Override
    @Transactional // Indica que este método debe ejecutarse dentro de una transacción
    public UsuarioResponseDTO createUsuario(UsuarioCreateDTO usuarioDto) {
        
        // 1. Validación de Negocio: Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            throw new DuplicateResourceException("El email '" + usuarioDto.getEmail() + "' ya está registrado.");
        }

        // 2. Mapeo DTO a Entity
        Usuario nuevoUsuario = usuarioMapper.toEntity(usuarioDto);

        // 3. Lógica de Negocio: Encriptación de Contraseña
        String hashedPassword = passwordEncoder.encode(usuarioDto.getPassword());
        nuevoUsuario.setPassword(hashedPassword);

        // 4. Lógica de Negocio: Asignación de valores por defecto/auditoría
        nuevoUsuario.setActivo(true); // Se activa por defecto
        Timestamp now = Timestamp.from(Instant.now());
        nuevoUsuario.setFechaCreacion(now);
        nuevoUsuario.setFechaActualizacion(now);

        // 5. Persistencia
        Usuario savedUsuario = usuarioRepository.save(nuevoUsuario);

        // 6. Mapeo Entity a Response DTO
        return usuarioMapper.toResponseDto(savedUsuario);
    }

    // --- Lógica de Lectura (READ) ---

    private Usuario findUsuarioById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true) // Transacción de solo lectura, optimiza el rendimiento
    public UsuarioResponseDTO getUsuarioById(Integer id) {
        Usuario usuario = findUsuarioById(id);
        return usuarioMapper.toResponseDto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> getAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Usamos el método de mapeo de lista que definimos en el Mapper
        return usuarioMapper.toResponseDtoList(usuarios);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO getUsuarioByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        return usuarioMapper.toResponseDto(usuario);
    }

    // --- Lógica de Actualización (UPDATE) ---

    @Override
    @Transactional
    public UsuarioResponseDTO updateUsuario(Integer id, UsuarioUpdateDTO usuarioDto) {
        
        Usuario usuarioToUpdate = findUsuarioById(id);

        // Lógica de Negocio: Verificar si el nuevo email ya existe en otro usuario
        if (usuarioDto.getEmail() != null && !usuarioDto.getEmail().equals(usuarioToUpdate.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
                throw new DuplicateResourceException("El nuevo email '" + usuarioDto.getEmail() + "' ya está en uso.");
            }
        }
        
        // Mapeo DTO a Entity (MapStruct solo actualiza los campos no nulos del DTO)
        usuarioMapper.updateEntityFromDto(usuarioDto, usuarioToUpdate);

        // Lógica de Negocio: Actualizar la fecha de modificación
        usuarioToUpdate.setFechaActualizacion(Timestamp.from(Instant.now()));

        // Persistencia
        Usuario updatedUsuario = usuarioRepository.save(usuarioToUpdate);

        // Mapeo Entity a Response DTO
        return usuarioMapper.toResponseDto(updatedUsuario);
    }

    // --- Lógica de Eliminación (DELETE) ---

    @Override
    @Transactional
    public void deleteUsuario(Integer id) {
        Usuario usuarioToDelete = findUsuarioById(id);
        usuarioRepository.delete(usuarioToDelete);
    }
}