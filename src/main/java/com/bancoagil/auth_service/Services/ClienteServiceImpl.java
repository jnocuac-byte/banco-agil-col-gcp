package com.bancoagil.auth_service.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.auth_service.Dtos.ClienteCreateDTO;
import com.bancoagil.auth_service.Dtos.ClienteResponseDTO;
import com.bancoagil.auth_service.Dtos.ClienteUpdateDTO;
import com.bancoagil.auth_service.Entities.Cliente;
import com.bancoagil.auth_service.Entities.Usuario;
import com.bancoagil.auth_service.Exceptions.ResourceNotFoundException;
import com.bancoagil.auth_service.Mappers.IClienteMapper;
import com.bancoagil.auth_service.Repository.IClienteRepository;
import com.bancoagil.auth_service.Repository.IUsuarioRepository;
import com.bancoagil.auth_service.ServiceInterface.IClienteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements IClienteService {

    private final IClienteRepository clienteRepository;
    private final IUsuarioRepository usuarioRepository; // Necesario para buscar el Usuario
    private final IClienteMapper clienteMapper;

    @Override
    @Transactional
    public ClienteResponseDTO createCliente(ClienteCreateDTO clienteDto) {
        
        // 1. Obtener la entidad Usuario completa (la necesitamos para la FK)
        Usuario usuario = usuarioRepository.findById(clienteDto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear el cliente. Usuario no encontrado con ID: " + clienteDto.getIdUsuario()));

        // 2. Verificar duplicidad (opcional, pero buena práctica si solo permites un Cliente por Usuario)
        // Buscamos si ya hay un Cliente asociado a este Usuario.
        if (usuario.getCliente() != null) { 
            throw new IllegalArgumentException("Ya existe un registro de cliente asociado al Usuario con ID: " + clienteDto.getIdUsuario());
        }

        // 3. Mapear la entidad Cliente
        Cliente nuevoCliente = clienteMapper.toClienteEntity(clienteDto);
        
        // 4. Establecer la relación bidireccional
        nuevoCliente.setUsuario(usuario); // Establece la FK id_usuario en la tabla clientes
        usuario.setCliente(nuevoCliente); // Establece el lado "mappedBy" en Usuario
        
        // 5. Guardar el nuevo Cliente (y el Usuario se actualiza en cascada)
        // El Cliente ahora tiene una PK generada por la DB, ¡sin conflicto con el Usuario!
        Cliente savedCliente = clienteRepository.save(nuevoCliente);

        // 6. Devolver DTO
        return clienteMapper.toResponseDto(savedCliente);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO getClienteById(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        return clienteMapper.toResponseDto(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> getAllClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clienteMapper.toResponseDtoList(clientes);
    }

    @Override
    @Transactional
    public ClienteResponseDTO updateCliente(Integer id, ClienteUpdateDTO clienteDto) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        // Aplicar los cambios del DTO al Entity
        clienteMapper.updateEntityFromDto(clienteDto, existingCliente);

        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return clienteMapper.toResponseDto(updatedCliente);
    }

    @Override
    @Transactional
    public void deleteCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
}