package com.bancoagil.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.auth_service.dto.ActualizarAsesorDTO;
import com.bancoagil.auth_service.dto.AsesorPerfilDTO;
import com.bancoagil.auth_service.dto.CambiarPasswordDTO;
import com.bancoagil.auth_service.model.Asesor;
import com.bancoagil.auth_service.model.Usuario;
import com.bancoagil.auth_service.repository.AsesorRepository;
import com.bancoagil.auth_service.repository.UsuarioRepository;

// Servicio para manejar la lógica de negocio relacionada con los asesores
@Service
public class AsesorService {

    // Repositorios y codificador de contraseñas inyectados
    @Autowired
    private AsesorRepository asesorRepository;

    // Repositorio de usuarios
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Codificador de contraseñas
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene el perfil completo del asesor
     */
    @Transactional(readOnly = true)
    // Obtener el perfil del asesor por ID
    public AsesorPerfilDTO obtenerPerfilAsesor(Long id) {
        // Buscar asesor por ID
        Asesor asesor = asesorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado")); // Lanzar excepción si no se encuentra

        // Buscar usuario asociado al asesor
        Usuario usuario = usuarioRepository.findById(asesor.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanzar excepción si no se encuentra

        // Mapear datos a DTO de perfil
        AsesorPerfilDTO dto = new AsesorPerfilDTO();
        dto.setAsesorId(asesor.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombres(asesor.getNombres());
        dto.setApellidos(asesor.getApellidos());
        dto.setCodigoEmpleado(asesor.getCodigoEmpleado());
        dto.setArea(asesor.getArea() != null ? asesor.getArea().name() : null); // Manejar posible null
        dto.setNombreCompleto(asesor.getNombres() + " " + asesor.getApellidos());

        // Devolver DTO con los datos del perfil
        return dto;
    }

    /**
     * Actualiza los datos del asesor (nombres, apellidos, área)
     */
    @Transactional
    // Actualizar datos del asesor
    public void actualizarAsesor(Long id, ActualizarAsesorDTO dto) {
        Asesor asesor = asesorRepository.findById(id) // Buscar asesor por ID
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado")); // Lanzar excepción si no se encuentra

        // Obtener área actual y nueva
        Asesor.Area areaActual = asesor.getArea();
        Asesor.Area areaNueva;

        // Validar que el área sea válida
        try {
            areaNueva = Asesor.Area.valueOf(dto.getArea()); // Convertir cadena a enum
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Área inválida. Valores permitidos: CREDITO, RIESGO, ADMINISTRACION, ADMIN_TOTAL"); // Lanzar excepción si el área no es válida
        }

        // Validar restricciones de cambio de área
        if (areaActual != null && !areaActual.equals(areaNueva)) {
            // Si el asesor tiene ADMIN_TOTAL, NO puede cambiar a otra área
            if (areaActual == Asesor.Area.ADMIN_TOTAL) {
                throw new RuntimeException("Un asesor con área ADMIN_TOTAL no puede cambiar a otra área"); // Lanzar excepción
            }

            // Si el asesor tiene otra área, NO puede cambiar a ADMIN_TOTAL
            if (areaNueva == Asesor.Area.ADMIN_TOTAL) {
                throw new RuntimeException("No se puede cambiar el área a ADMIN_TOTAL desde otra área"); // Lanzar excepción
            }
        }

        // Actualizar campos permitidos
        asesor.setArea(areaNueva);
        asesor.setNombres(dto.getNombres());
        asesor.setApellidos(dto.getApellidos());

        // Guardar cambios en la base de datos
        asesorRepository.save(asesor);
    }

    /**
     * Cambia la contraseña del asesor verificando la contraseña actual
     */
    @Transactional
    // Cambiar la contraseña del asesor
    public void cambiarPassword(Long id, CambiarPasswordDTO dto) {
        Asesor asesor = asesorRepository.findById(id) // Buscar asesor por ID
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado")); // Lanzar excepción si no se encuentra

        // Buscar usuario asociado al asesor
        Usuario usuario = usuarioRepository.findById(asesor.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanzar excepción si no se encuentra

        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta"); // Lanzar excepción si la contraseña no coincide
        }

        // Encriptar y guardar la nueva contraseña
        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }
}