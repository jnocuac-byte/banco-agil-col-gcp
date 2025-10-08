package com.bancoagil.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    // Registrar cliente
    @Transactional
    public Usuario registrarCliente(RegistroClienteDTO dto){

        if(usuarioRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("El email ya está en uso");
        }

        if("PERSONA_NATURAL".equals(dto.getTipoCliente())){
            validarDatosPersonaNatural(dto);
        } else if("EMPRESA".equals(dto.getTipoCliente())){
            validarDatosEmpresa(dto);
        }
        
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setIdUsuario(usuario.getId());
        cliente.setTipoCliente(Cliente.TipoCliente.valueOf(dto.getTipoCliente()));
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCiudad(dto.getCiudad());
        cliente= clienteRepository.save(cliente);

        if("PERSONA_NATURAL".equals(dto.getTipoCliente())){
            PersonaNatural personaNatural = new PersonaNatural();
            personaNatural.setIdCliente(cliente.getId());
            personaNatural.setNumDocumento(dto.getNumDocumento());
            personaNatural.setTipoDocumento(PersonaNatural.TipoDocumento.valueOf(dto.getTipoDocumento()));
            personaNatural.setNombres(dto.getNombres());
            personaNatural.setApellidos(dto.getApellidos());
            personaNatural.setFechaNacimiento(dto.getFechaNacimiento());
            personaNaturalRepository.save(personaNatural);
        } else if("EMPRESA".equals(dto.getTipoCliente())){
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

    private void validarDatosPersonaNatural(RegistroClienteDTO dto){
        if(dto.getNumDocumento() == null || dto.getNumDocumento().isBlank()){
            throw new RuntimeException("El número de documento es obligatorio para personas naturales");
        }
        if(dto.getTipoDocumento() == null || dto.getTipoDocumento().isBlank()){
            throw new RuntimeException("El tipo de documento es obligatorio para personas naturales");
        }
        if(dto.getNombres() == null || dto.getNombres().isBlank()){
            throw new RuntimeException("El nombre es obligatorio para personas naturales");
        }
        if(dto.getApellidos() == null || dto.getApellidos().isBlank()){
            throw new RuntimeException("Los apellidos son obligatorios para personas naturales");
        }
        if(dto.getFechaNacimiento() == null){
            throw new RuntimeException("La fecha de nacimiento es obligatoria para personas naturales");
        }
    }

    private void validarDatosEmpresa(RegistroClienteDTO dto){
        if(dto.getNit() == null || dto.getNit().isBlank()){
            throw new RuntimeException("El NIT es obligatorio para empresas");
        }
        if(dto.getRazonSocial() == null || dto.getRazonSocial().isBlank()){
            throw new RuntimeException("La razón social es obligatoria para empresas");
        }
        if(dto.getNombreComercial() == null || dto.getNombreComercial().isBlank()){
            throw new RuntimeException("El nombre comercial es obligatorio para empresas");
        }
        if(dto.getFechaConstitucion() == null){
            throw new RuntimeException("La fecha de constitución es obligatoria para empresas");
        }
        if(dto.getNumEmpleados() == null || dto.getNumEmpleados().isBlank()){
            throw new RuntimeException("El número de empleados es obligatorio para empresas");
        }
        if(dto.getSectorEconomico() == null || dto.getSectorEconomico().isBlank()){
            throw new RuntimeException("El sector económico es obligatorio para empresas");
        }
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
        
        if (!usuario.getPassword().equals(dto.getPassword())) {
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
            PersonaNatural persona = personaNaturalRepository.findById(cliente.getId())
                    .orElse(null);
            if (persona != null) {
                nombreCompleto = persona.getNombres() + " " + persona.getApellidos();
            }
        } else {
            Empresa empresa = empresaRepository.findById(cliente.getId())
                    .orElse(null);
            if (empresa != null) {
                nombreCompleto = empresa.getRazonSocial();
            }
        }
        
        String token = "mock-token-" + usuario.getId();
        
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
        

        if (!usuario.getPassword().equals(dto.getPassword())) {
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
        
        String token = "mock-token-asesor-" + usuario.getId();
        
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

}