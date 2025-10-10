package com.bancoagil.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.auth_service.dto.AsesorPerfilDTO;
import com.bancoagil.auth_service.dto.ActualizarAsesorDTO;
import com.bancoagil.auth_service.dto.CambiarPasswordDTO;
import com.bancoagil.auth_service.model.Asesor;
import com.bancoagil.auth_service.model.Usuario;
import com.bancoagil.auth_service.repository.AsesorRepository;
import com.bancoagil.auth_service.repository.UsuarioRepository;

@Service
public class AsesorService {

    @Autowired
    private AsesorRepository asesorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene el perfil completo del asesor
     */
    @Transactional(readOnly = true)
    public AsesorPerfilDTO obtenerPerfilAsesor(Long id) {
        Asesor asesor = asesorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));

        Usuario usuario = usuarioRepository.findById(asesor.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        AsesorPerfilDTO dto = new AsesorPerfilDTO();
        dto.setAsesorId(asesor.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombres(asesor.getNombres());
        dto.setApellidos(asesor.getApellidos());
        dto.setCodigoEmpleado(asesor.getCodigoEmpleado());
        dto.setArea(asesor.getArea() != null ? asesor.getArea().name() : null);
        dto.setNombreCompleto(asesor.getNombres() + " " + asesor.getApellidos());

        return dto;
    }

    /**
     * Actualiza los datos del asesor (nombres, apellidos, área)
     */
    @Transactional
    public void actualizarAsesor(Long id, ActualizarAsesorDTO dto) {
        Asesor asesor = asesorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));

        // Validar que el área sea válida
        try {
            Asesor.Area area = Asesor.Area.valueOf(dto.getArea());
            asesor.setArea(area);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Área inválida. Valores permitidos: CREDITO, RIESGO, ADMINISTRACION");
        }

        // Actualizar campos permitidos
        asesor.setNombres(dto.getNombres());
        asesor.setApellidos(dto.getApellidos());

        asesorRepository.save(asesor);
    }

    /**
     * Cambia la contraseña del asesor verificando la contraseña actual
     */
    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordDTO dto) {
        Asesor asesor = asesorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));

        Usuario usuario = usuarioRepository.findById(asesor.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Encriptar y guardar la nueva contraseña
        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }
}