package com.bancoagil.account_service.service;

import com.bancoagil.account_service.dto.ClienteDTO;
import com.bancoagil.account_service.dto.ClienteCompletoDTO;
import com.bancoagil.account_service.model.Cliente;
import com.bancoagil.account_service.repository.ClienteRepository;
import com.bancoagil.account_service.repository.UsuarioRepository;
import com.bancoagil.account_service.repository.PersonaNaturalRepository;
import com.bancoagil.account_service.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PersonaNaturalRepository personaNaturalRepository;
    
    @Autowired
    private EmpresaRepository empresaRepository;

    public List<ClienteDTO> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO getClienteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        return convertToDTO(cliente);
    }

    public ClienteDTO getClienteByIdUsuario(Integer idUsuario) {
        Cliente cliente = clienteRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con idUsuario: " + idUsuario));
        return convertToDTO(cliente);
    }
    
    public List<ClienteCompletoDTO> getAllClientesCompletos() {
        return clienteRepository.findAll().stream()
                .map(this::convertToClienteCompletoDTO)
                .collect(Collectors.toList());
    }
    
    public ClienteCompletoDTO getClienteCompletoById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        return convertToClienteCompletoDTO(cliente);
    }

    public ClienteDTO updateCliente(Long id, ClienteDTO clienteDTO) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));

        if (!existingCliente.getIdUsuario().equals(clienteDTO.getIdUsuario()) && 
            clienteRepository.existsByIdUsuario(clienteDTO.getIdUsuario())) {
            throw new RuntimeException("Ya existe un cliente con idUsuario: " + clienteDTO.getIdUsuario());
        }

        existingCliente.setIdUsuario(clienteDTO.getIdUsuario());
        existingCliente.setTelefono(clienteDTO.getTelefono());
        existingCliente.setTipoCliente(clienteDTO.getTipoCliente());
        existingCliente.setCiudad(clienteDTO.getCiudad());
        existingCliente.setDireccion(clienteDTO.getDireccion());

        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return convertToDTO(updatedCliente);
    }

    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }


    
    private ClienteCompletoDTO convertToClienteCompletoDTO(Cliente cliente) {
        ClienteCompletoDTO dto = new ClienteCompletoDTO();
        
        // Datos del cliente
        dto.setId(cliente.getId());
        dto.setIdUsuario(cliente.getIdUsuario());
        dto.setTipoCliente(cliente.getTipoCliente());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setCiudad(cliente.getCiudad());
        
        // Datos del usuario
        if (cliente.getUsuario() != null) {
            dto.setEmail(cliente.getUsuario().getEmail());
            dto.setActivo(cliente.getUsuario().getActivo());
            dto.setFechaCreacion(cliente.getUsuario().getFechaCreacion());
        } else {

            usuarioRepository.findById(cliente.getIdUsuario()).ifPresent(usuario -> {
                dto.setEmail(usuario.getEmail());
                dto.setActivo(usuario.getActivo());
                dto.setFechaCreacion(usuario.getFechaCreacion());
            });
        }
        
        // Datos específicos según el tipo
        if (cliente.getTipoCliente() == Cliente.TipoCliente.PERSONA_NATURAL) {
            personaNaturalRepository.findByIdCliente(cliente.getId()).ifPresent(persona -> {
                dto.setNumDocumento(persona.getNumDocumento());
                dto.setTipoDocumento(persona.getTipoDocumento());
                dto.setNombres(persona.getNombres());
                dto.setApellidos(persona.getApellidos());
                dto.setFechaNacimiento(persona.getFechaNacimiento());
            });
        } else if (cliente.getTipoCliente() == Cliente.TipoCliente.EMPRESA) {
            empresaRepository.findByIdCliente(cliente.getId()).ifPresent(empresa -> {
                dto.setNit(empresa.getNit());
                dto.setRazonSocial(empresa.getRazonSocial());
                dto.setNombreComercial(empresa.getNombreComercial());
                dto.setFechaConstitucion(empresa.getFechaConstitucion());
                dto.setNumEmpleados(empresa.getNumEmpleados());
                dto.setSectorEconomico(empresa.getSectorEconomico());
            });
        }
        
        return dto;
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setIdUsuario(cliente.getIdUsuario());
        dto.setTelefono(cliente.getTelefono());
        dto.setTipoCliente(cliente.getTipoCliente());
        dto.setCiudad(cliente.getCiudad());
        dto.setDireccion(cliente.getDireccion());
        return dto;
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setIdUsuario(dto.getIdUsuario());
        cliente.setTelefono(dto.getTelefono());
        cliente.setTipoCliente(dto.getTipoCliente());
        cliente.setCiudad(dto.getCiudad());
        cliente.setDireccion(dto.getDireccion());
        return cliente;
    }
}
