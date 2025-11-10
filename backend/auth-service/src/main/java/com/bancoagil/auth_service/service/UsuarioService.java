package com.bancoagil.auth_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bancoagil.auth_service.dto.AsesorDTO;
import com.bancoagil.auth_service.dto.ClienteDTO;
import com.bancoagil.auth_service.dto.ClienteDetalleDTO;
import com.bancoagil.auth_service.dto.EstadisticasAsesorDTO;
import com.bancoagil.auth_service.dto.SolicitudDTO;
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

// Servicio para manejar la lógica de negocio relacionada con los usuarios
@Service
public class UsuarioService {

    // Repositorios y RestTemplate inyectados
    @Autowired
    private ClienteRepository clienteRepository;

    // Repositorio de usuarios
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Repositorio de personas naturales
    @Autowired
    private PersonaNaturalRepository personaNaturalRepository;

    // Repositorio de empresas
    @Autowired
    private EmpresaRepository empresaRepository;

    // Repositorio de asesores
    @Autowired
    private AsesorRepository asesorRepository;

    // Cliente REST para comunicarse con el servicio de créditos
    @Autowired
    private RestTemplate restTemplate;

    // URL base del servicio de créditos
    private static final String CREDIT_SERVICE_URL = "http://localhost:8081/api/solicitudes";

    // Método para listar clientes
    @Transactional(readOnly = true)
    // Listar todos los clientes
    public List<ClienteDTO> listarClientes() {
        // Obtener todos los clientes
        List<Cliente> clientes = clienteRepository.findAll();
        
        // Mapear a DTOs y devolver la lista
        return clientes.stream().map(cliente -> { // Mapear cada cliente a ClienteDTO
            Usuario usuario = usuarioRepository.findById(cliente.getIdUsuario()) // Buscar usuario asociado
                    .orElse(null); // Si no se encuentra, devolver null
            
            // Si no se encuentra el usuario, devolver null
            if (usuario == null) return null;
            
            // Crear y llenar el DTO
            ClienteDTO dto = new ClienteDTO();
            dto.setClienteId(cliente.getId());
            dto.setEmail(usuario.getEmail());
            dto.setActivo(usuario.getActivo());
            dto.setTipoCliente(cliente.getTipoCliente().name());
            dto.setTelefono(cliente.getTelefono());
            dto.setCiudad(cliente.getCiudad());
            dto.setDocumentoIdentidadEstado(cliente.getDocumentoIdentidadEstado());
            
            // Obtener nombre completo y documento según el tipo de cliente
            if (cliente.getTipoCliente() == Cliente.TipoCliente.PERSONA_NATURAL) {
                PersonaNatural persona = personaNaturalRepository.findByIdCliente(cliente.getId()) // Buscar persona natural
                        .orElse(null); // Si no se encuentra, devolver null
                if (persona != null) { // Si se encuentra, llenar los datos
                    dto.setNombreCompleto(persona.getNombres() + " " + persona.getApellidos()); // Combinar nombres y apellidos
                    dto.setDocumento(persona.getNumDocumento()); // Documento de identidad
                }
            } else { // Si es empresa
                Empresa empresa = empresaRepository.findByIdCliente(cliente.getId()) // Buscar empresa
                        .orElse(null); // Si no se encuentra, devolver null
                if (empresa != null) { // Si se encuentra, llenar los datos
                    dto.setNombreCompleto(empresa.getRazonSocial()); // Razón social como nombre
                    dto.setDocumento(empresa.getNit()); // NIT como documento
                }
            }
            
            // Devolver el DTO lleno
            return dto;
        }).filter(dto -> dto != null).collect(Collectors.toList()); // Filtrar nulos y colectar en lista
    }

    // Método para obtener detalles de un cliente por ID
    @Transactional(readOnly = true)
    // Obtener detalles de un cliente por ID
    public ClienteDetalleDTO obtenerClienteDetalle(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId) // Buscar cliente por ID
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")); // Lanzar excepción si no se encuentra
        
        // Buscar usuario asociado al cliente
        Usuario usuario = usuarioRepository.findById(cliente.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanzar excepción si no se encuentra
        
        // Mapear datos a DTO de detalles
        ClienteDetalleDTO dto = new ClienteDetalleDTO();
        dto.setClienteId(cliente.getId());
        dto.setEmail(usuario.getEmail());
        dto.setActivo(usuario.getActivo());
        dto.setTipoCliente(cliente.getTipoCliente().name());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setCiudad(cliente.getCiudad());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        
        // Obtener nombre completo y documento según el tipo de cliente
        if (cliente.getTipoCliente() == Cliente.TipoCliente.PERSONA_NATURAL) {
            // Buscar persona natural asociada
            PersonaNatural persona = personaNaturalRepository.findByIdCliente(cliente.getId())
                    .orElse(null); // Si no se encuentra, devolver null

            // Si se encuentra, llenar los datos
            if (persona != null) {
                dto.setNombreCompleto(persona.getNombres() + " " + persona.getApellidos()); // Combinar nombres y apellidos
                dto.setDocumento(persona.getNumDocumento()); // Documento de identidad
            }
        } else { // Si es empresa
            // Buscar empresa asociada
            Empresa empresa = empresaRepository.findByIdCliente(cliente.getId())
                    .orElse(null); // Si no se encuentra, devolver null
            if (empresa != null) { // Si se encuentra, llenar los datos
                dto.setNombreCompleto(empresa.getRazonSocial()); // Razón social como nombre
                dto.setDocumento(empresa.getNit()); // NIT como documento
            }
        }
        
        // Devolver el DTO lleno
        return dto;
    }

    // Método para obtener solicitudes de un cliente por ID
    @Transactional(readOnly = true)
    // Obtener solicitudes de un cliente por ID
    public List<SolicitudDTO> obtenerSolicitudesCliente(Long clienteId) {
        // Hacer llamada REST al servicio de créditos
        try {
            // Construir URL con el ID del cliente
            String url = CREDIT_SERVICE_URL + "/cliente/" + clienteId;
            
            // Hacer la llamada GET y obtener la respuesta
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange( // Hacer la llamada REST
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {} // Tipo de respuesta esperada
            );
            
            // Mapear la respuesta a una lista de SolicitudDTO
            List<Map<String, Object>> solicitudes = response.getBody();
            
            // Si no hay solicitudes, devolver lista vacía
            if (solicitudes == null) {
                return new ArrayList<>();
            }
            
            // Mapear cada solicitud a SolicitudDTO y devolver la lista
            return solicitudes.stream().map(sol -> {
                // Mapear cada campo de la solicitud al DTO
                SolicitudDTO dto = new SolicitudDTO();
                dto.setId(((Number) sol.get("id")).longValue()); // Convertir a Long
                dto.setMontoSolicitado(new java.math.BigDecimal(sol.get("montoSolicitado").toString())); // Convertir a BigDecimal
                dto.setPlazoMeses((Integer) sol.get("plazoMeses")); // Plazo en meses
                dto.setTasaInteres(sol.get("tasaInteres") != null ?  // Convertir a BigDecimal si no es null
                    new java.math.BigDecimal(sol.get("tasaInteres").toString()) : null); // Manejar posible null
                dto.setEstado((String) sol.get("estado")); // Estado de la solicitud
                dto.setFechaSolicitud(java.time.LocalDateTime.parse(sol.get("fechaSolicitud").toString())); // Parsear fecha y hora
                return dto; // Devolver el DTO
            }).collect(Collectors.toList()); // Colectar en lista y devolver
            
        } catch (org.springframework.web.client.RestClientException | java.time.format.DateTimeParseException e) { // Manejar errores
            // En caso de error, devolver lista vacía
            return new ArrayList<>();
        }
    }

    // Método para cambiar el estado (activo/inactivo) de un cliente
    @Transactional
    // Cambiar el estado (activo/inactivo) de un cliente
    public void cambiarEstadoCliente(Long clienteId, Boolean nuevoEstado) {
        // Buscar cliente por ID
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")); // Lanzar excepción si no se encuentra
        
        // Buscar usuario asociado al cliente
        Usuario usuario = usuarioRepository.findById(cliente.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanzar excepción si no se encuentra
        
        // Actualizar y guardar el estado
        usuario.setActivo(nuevoEstado);
        usuarioRepository.save(usuario);
    }
    
    // Método para cambiar el estado del documento de un cliente
    @Transactional
    public void cambiarEstadoDocumentoCliente(Long clienteId, String nuevoEstadoDocumento) {
        // Buscar cliente por ID
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + clienteId));

        cliente.setDocumentoIdentidadEstado(nuevoEstadoDocumento);
        clienteRepository.save(cliente);
    }

    // Método para listar asesores
    @Transactional(readOnly = true)
    // Listar todos los asesores
    public List<AsesorDTO> listarAsesores() {
        // Obtener todos los asesores
        List<Asesor> asesores = asesorRepository.findAll();
        
        // Mapear a DTOs y devolver la lista
        return asesores.stream().map(asesor -> {
            // Buscar usuario asociado al asesor
            Usuario usuario = usuarioRepository.findById(asesor.getIdUsuario())
                    .orElse(null); // Si no se encuentra, devolver null
            
            // Si no se encuentra el usuario, devolver null
            if (usuario == null) return null;
            
            // Crear y llenar el DTO
            AsesorDTO dto = new AsesorDTO();
            dto.setAsesorId(asesor.getId());
            dto.setEmail(usuario.getEmail());
            dto.setActivo(usuario.getActivo());
            dto.setNombreCompleto(asesor.getNombres() + " " + asesor.getApellidos());
            dto.setCodigoEmpleado(asesor.getCodigoEmpleado());
            dto.setArea(asesor.getArea() != null ? asesor.getArea().name() : null);
            
            // Devolver el DTO lleno
            return dto;
        }).filter(dto -> dto != null).collect(Collectors.toList()); // Filtrar nulos y colectar en lista
    }

    // Método para obtener un asesor por ID
    @Transactional(readOnly = true)
    // Obtener un asesor por ID
    public AsesorDTO obtenerAsesor(Long asesorId) {
        // Buscar asesor por ID
        Asesor asesor = asesorRepository.findById(asesorId)
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado")); // Lanzar excepción si no se encuentra
        
        // Buscar usuario asociado al asesor
        Usuario usuario = usuarioRepository.findById(asesor.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanzar excepción si no se encuentra
        
        // Mapear datos a DTO
        AsesorDTO dto = new AsesorDTO();
        dto.setAsesorId(asesor.getId());
        dto.setEmail(usuario.getEmail());
        dto.setActivo(usuario.getActivo());
        dto.setNombreCompleto(asesor.getNombres() + " " + asesor.getApellidos());
        dto.setCodigoEmpleado(asesor.getCodigoEmpleado());
        dto.setArea(asesor.getArea() != null ? asesor.getArea().name() : null);
        
        // Devolver el DTO lleno
        return dto;
    }

    // Método para obtener estadísticas de un asesor
    @Transactional(readOnly = true)
    public EstadisticasAsesorDTO obtenerEstadisticasAsesor(Long asesorId) { // Obtener estadísticas de un asesor por ID
        // Verificar que el asesor exista
        @SuppressWarnings("unused") // Evitar advertencia de variable no usada
        Asesor asesor = asesorRepository.findById(asesorId) // Buscar asesor por ID
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado")); // Lanzar excepción si no se encuentra
        
        // Hacer llamada REST al servicio de créditos para obtener todas las solicitudes
        try {
            // Construir URL para obtener todas las solicitudes
            String url = CREDIT_SERVICE_URL + "/todas";
            
            // Hacer la llamada GET y obtener la respuesta
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange( // Hacer la llamada REST
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {} // Tipo de respuesta esperada
            );
            
            // Procesar la respuesta para calcular estadísticas
            List<Map<String, Object>> todasSolicitudes = response.getBody();
            
            // Si no hay solicitudes, inicializar lista vacía
            if (todasSolicitudes == null) {
                todasSolicitudes = new ArrayList<>(); 
            }
            
            // Filtrar y contar solicitudes asignadas al asesor
            long totalProcesadas = todasSolicitudes.stream()
                    .filter(s -> s.get("idAsesorAsignado") != null &&  // Verificar que no sea null
                                ((Number) s.get("idAsesorAsignado")).longValue() == asesorId) // Comparar IDs
                    .filter(s -> !s.get("estado").equals("PENDIENTE")) // Excluir pendientes
                    .count(); // Contar total procesadas
            
            // Filtrar y contar solicitudes aprobadas por el asesor
            long aprobadas = todasSolicitudes.stream()
                    .filter(s -> s.get("idAsesorAsignado") != null &&  // Verificar que no sea null
                                ((Number) s.get("idAsesorAsignado")).longValue() == asesorId) // Comparar IDs
                    .filter(s -> s.get("estado").equals("APROBADA")) // Solo aprobadas
                    .count(); // Contar aprobadas
            
            // Crear y devolver el DTO con las estadísticas
            EstadisticasAsesorDTO dto = new EstadisticasAsesorDTO();
            dto.setTotalProcesadas(totalProcesadas);
            dto.setAprobadas(aprobadas);
            
            // Devolver el DTO lleno
            return dto;
            
        } catch (org.springframework.web.client.RestClientException | java.time.format.DateTimeParseException e) { // Manejar errores
            EstadisticasAsesorDTO dto = new EstadisticasAsesorDTO(); // En caso de error, devolver estadísticas en cero
            dto.setTotalProcesadas(0L); // Total procesadas en cero
            dto.setAprobadas(0L); // Aprobadas en cero
            return dto; // Devolver el DTO con ceros
        }
    }

    @Transactional
    public void cambiarEstadoAsesor(Long asesorId, Boolean nuevoEstado) {
        Asesor asesor = asesorRepository.findById(asesorId) // Buscar asesor por ID
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado")); // Lanzar excepción si no se encuentra
        
        Usuario usuario = usuarioRepository.findById(asesor.getIdUsuario()) // Buscar usuario asociado
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanzar excepción si no se encuentra
        
        // Actualizar y guardar el estado
        usuario.setActivo(nuevoEstado);
        usuarioRepository.save(usuario);
    }
}