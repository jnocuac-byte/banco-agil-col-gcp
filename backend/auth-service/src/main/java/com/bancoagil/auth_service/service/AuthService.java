package com.bancoagil.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.auth_service.dto.LoginDTO;
import com.bancoagil.auth_service.dto.LoginResponseDTO;
import com.bancoagil.auth_service.dto.RegistroClienteDTO;
import com.bancoagil.auth_service.model.Asesor;
import com.bancoagil.auth_service.model.Cliente;
import com.bancoagil.auth_service.model.Empresa;
import com.bancoagil.auth_service.model.PersonaNatural;
import com.bancoagil.auth_service.model.Usuario;
import com.bancoagil.auth_service.repository.AsesorRepository;
import com.bancoagil.auth_service.repository.ClienteRepository;
import com.bancoagil.auth_service.repository.EmpresaRepository;
import com.bancoagil.auth_service.repository.PersonaNaturalRepository;
import com.bancoagil.auth_service.repository.UsuarioRepository;
import com.bancoagil.auth_service.util.JwtUtil;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PersonaNaturalRepository personaNaturalRepository;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    @Autowired
    private AsesorRepository asesorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional
    public Usuario registrarCliente(RegistroClienteDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        if (null == dto.getTipoCliente()) {
            throw new RuntimeException("Tipo de cliente inválido");
        } else switch (dto.getTipoCliente()) {
            case "PERSONA_NATURAL" -> validarDatosPersonaNatural(dto);
            case "EMPRESA" -> validarDatosEmpresa(dto);
            default -> throw new RuntimeException("Tipo de cliente inválido");
        }
        
        // Crear Usuario con contraseña encriptada
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); // ← ENCRIPTAR
        usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);
        
        // Crear Cliente
        Cliente cliente = new Cliente();
        cliente.setIdUsuario(usuario.getId());
        cliente.setTipoCliente(Cliente.TipoCliente.valueOf(dto.getTipoCliente()));
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCiudad(dto.getCiudad());
        cliente = clienteRepository.save(cliente);
        
        // Crear Persona Natural o Empresa
        if ("PERSONA_NATURAL".equals(dto.getTipoCliente())) {
            PersonaNatural persona = new PersonaNatural();
            persona.setIdCliente(cliente.getId());
            persona.setNumDocumento(dto.getNumDocumento());
            persona.setTipoDocumento(PersonaNatural.TipoDocumento.valueOf(dto.getTipoDocumento()));
            persona.setNombres(dto.getNombres());
            persona.setApellidos(dto.getApellidos());
            persona.setFechaNacimiento(dto.getFechaNacimiento());
            personaNaturalRepository.save(persona);
        } else {
            Empresa empresa = new Empresa();
            empresa.setIdCliente(cliente.getId());
            empresa.setNit(dto.getNit());
            empresa.setRazonSocial(dto.getRazonSocial());
            empresa.setNombreComercial(dto.getNombreComercial());
            empresa.setFechaConstitucion(dto.getFechaConstitucion());
            empresa.setNumEmpleados(dto.getNumEmpleados());
            empresa.setSectorEconomico(dto.getSectorEconomico());
            empresaRepository.save(empresa);
        }
        
        return usuario;
    }
    
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
        
        // Verificar contraseña con BCrypt
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
        
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador");
        }
        
        if (usuario.getTipoUsuario() != Usuario.TipoUsuario.CLIENTE) {
            throw new RuntimeException("Acceso no autorizado. Use el portal de asesores");
        }
        
        Cliente cliente = clienteRepository.findByIdUsuario(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Información de cliente no encontrada"));
        
        String nombreCompleto = "";
        
        if (cliente.getTipoCliente() == Cliente.TipoCliente.PERSONA_NATURAL) {
            PersonaNatural persona = personaNaturalRepository.findById(cliente.getId()).orElse(null);
            if (persona != null) {
                nombreCompleto = persona.getNombres() + " " + persona.getApellidos();
            }
        } else {
            Empresa empresa = empresaRepository.findById(cliente.getId()).orElse(null);
            if (empresa != null) {
                nombreCompleto = empresa.getRazonSocial();
            }
        }
        
        // Generar JWT token
        String token = jwtUtil.generateToken(usuario.getId(), usuario.getEmail(), usuario.getTipoUsuario().name());
        
        return new LoginResponseDTO(
            true,
            "Login exitoso",
            usuario.getId(),
            usuario.getEmail(),
            usuario.getTipoUsuario().name(),
            token,
            cliente.getId(),
            cliente.getTipoCliente().name(),
            nombreCompleto
        );
    }
    
    @Transactional(readOnly = true)
    public LoginResponseDTO loginAsesor(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
        
        // Verificar contraseña con BCrypt
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
        
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador");
        }
        
        if (usuario.getTipoUsuario() != Usuario.TipoUsuario.ASESOR) {
            throw new RuntimeException("Acceso no autorizado. Use el portal de clientes");
        }
        
        Asesor asesor = asesorRepository.findByIdUsuario(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Información de asesor no encontrada"));
        
        String nombreCompleto = asesor.getNombres() + " " + asesor.getApellidos();
        
        // Generar JWT token
        String token = jwtUtil.generateToken(usuario.getId(), usuario.getEmail(), usuario.getTipoUsuario().name());
        
        return new LoginResponseDTO(
            true,
            "Login exitoso",
            usuario.getId(),
            usuario.getEmail(),
            usuario.getTipoUsuario().name(),
            token,
            asesor.getId(),
            "ASESOR",
            nombreCompleto
        );
    }

   /* CREAR ADMIN ASESOR INICIAL 
    @Transactional
    public void crearAsesorInicial() {
        // Verificar si ya existe
        if (usuarioRepository.existsByEmail("asesor@bancoagil.com")) {
            // Actualizar solo la contraseña
            Usuario usuario = usuarioRepository.findByEmail("asesor@bancoagil.com").get();
            usuario.setPassword(passwordEncoder.encode("Admin123!"));
            usuarioRepository.save(usuario);
            return;
        }
        
        // Crear nuevo
        Usuario usuario = new Usuario();
        usuario.setEmail("asesor@bancoagil.com");
        usuario.setPassword(passwordEncoder.encode("Admin123!"));
        usuario.setTipoUsuario(Usuario.TipoUsuario.ASESOR);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);
        
        Asesor asesor = new Asesor();
        asesor.setIdUsuario(usuario.getId());
        asesor.setNombres("Ana");
        asesor.setApellidos("Martínez");
        asesor.setCodigoEmpleado("EMP001");
        asesor.setArea(Asesor.Area.CREDITO);
        asesorRepository.save(asesor);
    }*/

    
    private void validarDatosPersonaNatural(RegistroClienteDTO dto) {
        if (dto.getNumDocumento() == null || dto.getNumDocumento().isBlank()) {
            throw new RuntimeException("El número de documento es obligatorio para personas naturales");
        }
        if (dto.getNombres() == null || dto.getNombres().isBlank()) {
            throw new RuntimeException("Los nombres son obligatorios");
        }
        if (dto.getApellidos() == null || dto.getApellidos().isBlank()) {
            throw new RuntimeException("Los apellidos son obligatorios");
        }
    }
    
    private void validarDatosEmpresa(RegistroClienteDTO dto) {
        if (dto.getNit() == null || dto.getNit().isBlank()) {
            throw new RuntimeException("El NIT es obligatorio para empresas");
        }
        if (dto.getRazonSocial() == null || dto.getRazonSocial().isBlank()) {
            throw new RuntimeException("La razón social es obligatoria");
        }
        if (dto.getNumEmpleados() == null) { 
            throw new RuntimeException("El número de empleados es obligatorio para empresas");
        }
    }
}