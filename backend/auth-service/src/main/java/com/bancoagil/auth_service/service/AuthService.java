package com.bancoagil.auth_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.auth_service.dto.LoginDTO;
import com.bancoagil.auth_service.dto.LoginResponseDTO;
import com.bancoagil.auth_service.dto.RegistroClienteDTO;
import com.bancoagil.auth_service.model.Asesor;
import com.bancoagil.auth_service.model.Cliente;
import com.bancoagil.auth_service.model.Cuenta;
import com.bancoagil.auth_service.model.Empresa;
import com.bancoagil.auth_service.model.PersonaNatural;
import com.bancoagil.auth_service.model.Usuario;
import com.bancoagil.auth_service.repository.AsesorRepository;
import com.bancoagil.auth_service.repository.ClienteRepository;
import com.bancoagil.auth_service.repository.CuentaRepository;
import com.bancoagil.auth_service.repository.EmpresaRepository;
import com.bancoagil.auth_service.repository.PersonaNaturalRepository;
import com.bancoagil.auth_service.repository.UsuarioRepository;
import com.bancoagil.auth_service.util.JwtUtil;

// Servicio para manejar la lógica de negocio relacionada con autenticación y registro
@Service
public class AuthService {
    
    // Repositorios y utilidades inyectadas
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Repositorio de clientes
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Repositorio de personas naturales
    @Autowired
    private PersonaNaturalRepository personaNaturalRepository;
    
    // Repositorio de empresas
    @Autowired
    private EmpresaRepository empresaRepository;
    
    // Repositorio de asesores
    @Autowired
    private AsesorRepository asesorRepository;
    
    // Codificador de contraseñas
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Utilidad para manejar JWT
    @Autowired
    private JwtUtil jwtUtil;

    // Repositorio de cuentas
    @Autowired
    private CuentaRepository cuentaRepository;
    
    // Registrar un nuevo cliente
    @Transactional
    public Usuario registrarCliente(RegistroClienteDTO dto) {
        // Validar que el email no esté ya registrado
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado"); // Lanzar excepción si el email ya existe
        }
        
        // Validar tipo de cliente y datos obligatorios
        if (null == dto.getTipoCliente()) {
            throw new RuntimeException("Tipo de cliente inválido"); // Lanzar excepción si el tipo de cliente es nulo
        } else switch (dto.getTipoCliente()) { // Validar según el tipo de cliente
            case "PERSONA_NATURAL" -> validarDatosPersonaNatural(dto); // Validar datos específicos para persona natural
            case "EMPRESA" -> validarDatosEmpresa(dto); // Validar datos específicos para empresa
            default -> throw new RuntimeException("Tipo de cliente inválido"); // Lanzar excepción si el tipo de cliente es inválido
        }
        
        // Crear Usuario con contraseña encriptada
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Encriptar contraseña con BCrypt
        usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);
        
        // Crear Cliente
        Cliente cliente = new Cliente();
        cliente.setIdUsuario(usuario.getId());
        cliente.setTipoCliente(Cliente.TipoCliente.valueOf(dto.getTipoCliente())); // Convertir cadena a enum
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCiudad(dto.getCiudad());
        cliente = clienteRepository.save(cliente);
        
        // Crear Persona Natural o Empresa
        if ("PERSONA_NATURAL".equals(dto.getTipoCliente())) {
            PersonaNatural persona = new PersonaNatural();
            persona.setIdCliente(cliente.getId());
            persona.setNumDocumento(dto.getNumDocumento());
            persona.setTipoDocumento(PersonaNatural.TipoDocumento.valueOf(dto.getTipoDocumento())); // Convertir cadena a enum
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
        
        // Crear cuenta de ahorros automáticamente
        try {
            crearCuentaAhorros(cliente.getId()); // Crear cuenta de ahorros para el cliente
        } catch (Exception e) {
            throw new RuntimeException("Error al crear cuenta de ahorros: " + e.getMessage()); // Lanzar excepción si hay error al crear la cuenta
        }
            
        // Devolver el usuario creado
        return usuario;
    }
    
    // Autenticar un cliente y devolver token JWT
    @Transactional(readOnly = true)
    // Login de cliente
    public LoginResponseDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail()) // Buscar usuario por email
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos")); // Lanzar excepción si no se encuentra
        
        // Verificar contraseña con BCrypt
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos"); // Lanzar excepción si la contraseña no coincide
        }
        
        // Verificar que el usuario esté activo y sea cliente
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador"); // Lanzar excepción si el usuario está inactivo
        }
        
        // Verificar que sea cliente
        if (usuario.getTipoUsuario() != Usuario.TipoUsuario.CLIENTE) {
            throw new RuntimeException("Acceso no autorizado. Use el portal de asesores"); // Lanzar excepción si no es cliente
        }
        
        // Obtener información del cliente
        Cliente cliente = clienteRepository.findByIdUsuario(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Información de cliente no encontrada")); // Lanzar excepción si no se encuentra
        
        // Obtener nombre completo según tipo de cliente
        String nombres = "";
        String apellidos = "";
        LocalDate fechaNacimiento = null;
        
        // Si es persona natural, combinar nombres y apellidos
        if (cliente.getTipoCliente() == Cliente.TipoCliente.PERSONA_NATURAL) {
            // Buscar persona natural asociada al cliente
            PersonaNatural persona = personaNaturalRepository.findByIdCliente(cliente.getId()).orElse(null);
            // Si se encontró, combinar nombres y apellidos
            if (persona != null) {
                nombres = persona.getNombres();
                apellidos = persona.getApellidos();
                fechaNacimiento = persona.getFechaNacimiento();
            }
        } else { // Si es empresa, usar razón social
            // Buscar empresa asociada al cliente
            Empresa empresa = empresaRepository.findById(cliente.getId()).orElse(null);
            // Si se encontró, usar razón social
            if (empresa != null) {
                nombres = empresa.getNombreComercial();
                apellidos = empresa.getRazonSocial();
                fechaNacimiento = empresa.getFechaConstitucion();
            }
        }
        
        // Generar JWT token
        String token = jwtUtil.generateToken(usuario.getId(), usuario.getEmail(), usuario.getTipoUsuario().name());
        
        // Devolver datos de login con token
        return new LoginResponseDTO(
            true,
            "Login exitoso",
            usuario.getId(),
            usuario.getEmail(),
            usuario.getTipoUsuario().name(),
            token,
            cliente.getId(),
            cliente.getTipoCliente().name(),
            nombres,
            apellidos,
            fechaNacimiento,
            cliente.getCiudad(),
            cliente.getDireccion(),
            cliente.getDocumentoIdentidadEstado()
        );
    }
    
    // Login de asesor
    @Transactional(readOnly = true)
    public LoginResponseDTO loginAsesor(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail()) // Buscar usuario por email
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos")); // Lanzar excepción si no se encuentra
        
        // Verificar contraseña con BCrypt
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos"); // Lanzar excepción si la contraseña no coincide
        }
        
        // Verificar que el usuario esté activo y sea asesor
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador"); // Lanzar excepción si el usuario está inactivo
        }
        
        // Verificar que sea asesor
        if (usuario.getTipoUsuario() != Usuario.TipoUsuario.ASESOR) {
            throw new RuntimeException("Acceso no autorizado. Use el portal de clientes"); // Lanzar excepción si no es asesor
        }
        
        // Obtener información del asesor
        Asesor asesor = asesorRepository.findByIdUsuario(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Información de asesor no encontrada")); // Lanzar excepción si no se encuentra
        
        // Combinar nombres y apellidos
        String nombres = asesor.getNombres();
        String apellidos = asesor.getApellidos();
        
        // Generar JWT token
        String token = jwtUtil.generateToken(usuario.getId(), usuario.getEmail(), usuario.getTipoUsuario().name());
        
        // Devolver datos de login con token
        return new LoginResponseDTO(
            true,
            "Login exitoso",
            usuario.getId(),
            usuario.getEmail(),
            usuario.getTipoUsuario().name(),
            token,
            asesor.getId(),
            "ASESOR",
            nombres,
            apellidos,
            null,
            null,
            null,
            null
        );
    }

    // Crear asesor inicial si no existe
    @Transactional
    public void crearAsesorInicial() {
        // Verificar si ya existe
        if (usuarioRepository.existsByEmail("adminUC@bancoagil.com")) {
            // Actualizar solo la contraseña
            Usuario usuario = usuarioRepository.findByEmail("adminUC@bancoagil.com").get();
            usuario.setPassword(passwordEncoder.encode("Admin_UC!"));
            usuarioRepository.save(usuario);
            return;
        }
        
        // Crear nuevo
        Usuario usuario = new Usuario();
        usuario.setEmail("adminUC@bancoagil.com");
        usuario.setPassword(passwordEncoder.encode("Admin_UC!"));
        usuario.setTipoUsuario(Usuario.TipoUsuario.ASESOR);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);
        
        Asesor asesor = new Asesor();
        asesor.setIdUsuario(usuario.getId());
        asesor.setNombres("Admin");
        asesor.setApellidos("UC");
        asesor.setCodigoEmpleado("EMP000");
        asesor.setArea(Asesor.Area.ADMIN_TOTAL);
        asesorRepository.save(asesor);
    }

    // Método para generar número de cuenta
    private String generarNumeroCuenta(Long idCliente) {
        String codigoBanco = "001";           // Código fijo del banco
        String tipoCuenta = "1";              // 1 = Ahorros, 2 = Corriente
        String secuencia = String.format("%010d", idCliente); // Rellenar con ceros a la izquierda (10 dígitos)

        return codigoBanco + tipoCuenta + secuencia; // Total: 14 dígitos
    }

    // Método para crear cuenta de ahorros
    private Cuenta crearCuentaAhorros(Long idCliente) {
        Cuenta cuenta = new Cuenta();
        cuenta.setIdCliente(idCliente);
        cuenta.setTipoCuenta(Cuenta.TipoCuenta.AHORROS);
        cuenta.setSaldoActual(BigDecimal.ZERO);
        cuenta.setEstado(Cuenta.Estado.ACTIVA);
        
        // Generar número de cuenta basado en el ID
        String numeroCuenta = generarNumeroCuenta(idCliente);
        cuenta.setNumeroCuenta(numeroCuenta);
        
        // Actualizar con el número de cuenta
        return cuentaRepository.save(cuenta);
    }
    
    // Validaciones de datos según tipo de cliente
    private void validarDatosPersonaNatural(RegistroClienteDTO dto) {
        // Validar datos obligatorios para persona natural
        if (dto.getNumDocumento() == null || dto.getNumDocumento().isBlank()) {
            throw new RuntimeException("El número de documento es obligatorio para personas naturales"); // Lanzar excepción si el número de documento es nulo o vacío
        }
        // Validar tipo de documento
        if (dto.getNombres() == null || dto.getNombres().isBlank()) {
            throw new RuntimeException("Los nombres son obligatorios"); // Lanzar excepción si los nombres son nulos o vacíos
        }
        // Validar apellidos
        if (dto.getApellidos() == null || dto.getApellidos().isBlank()) {
            throw new RuntimeException("Los apellidos son obligatorios"); // Lanzar excepción si los apellidos son nulos o vacíos
        }
    }
    
    // Validar datos obligatorios para empresa
    private void validarDatosEmpresa(RegistroClienteDTO dto) {
        // Validar datos obligatorios para empresa
        if (dto.getNit() == null || dto.getNit().isBlank()) {
            throw new RuntimeException("El NIT es obligatorio para empresas"); // Lanzar excepción si el NIT es nulo o vacío
        }
        // Validar razón social
        if (dto.getRazonSocial() == null || dto.getRazonSocial().isBlank()) {
            throw new RuntimeException("La razón social es obligatoria"); // Lanzar excepción si la razón social es nula o vacía
        }
        // Validar nombre comercial
        if (dto.getNombreComercial() == null || dto.getNombreComercial().isBlank()) {
            throw new RuntimeException("El nombre comercial es obligatorio para empresas");
        }
        // Validar número de empleados
        if (dto.getNumEmpleados() == null) { 
            throw new RuntimeException("El número de empleados es obligatorio para empresas"); // Lanzar excepción si el número de empleados es nulo
        }
    }
}