package com.bancoagil.auth_service.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.auth_service.Dtos.PersonaNaturalCreateDTO;
import com.bancoagil.auth_service.Dtos.PersonaNaturalResponseDTO;
import com.bancoagil.auth_service.Dtos.PersonaNaturalUpdateDTO;
import com.bancoagil.auth_service.Entities.Cliente;
import com.bancoagil.auth_service.Entities.PersonaNatural;
import com.bancoagil.auth_service.Exceptions.ResourceNotFoundException;
import com.bancoagil.auth_service.Mappers.IPersonaNaturalMapper;
import com.bancoagil.auth_service.Repository.IClienteRepository;
import com.bancoagil.auth_service.Repository.IPersonaNaturalRepository;
import com.bancoagil.auth_service.ServiceInterface.IPersonaNaturalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Crea constructor con inyección para campos 'final'
public class PersonaNaturalServiceImpl implements IPersonaNaturalService {

    private final IPersonaNaturalRepository personaNaturalRepository;
    private final IClienteRepository clienteRepository;
    private final IPersonaNaturalMapper personaNaturalMapper;

    // ====================================================================
    // 1. CREATE
    // ====================================================================
    @Override
    @Transactional
    public PersonaNaturalResponseDTO create(PersonaNaturalCreateDTO dto) {
        
        // 1. Buscar y validar la existencia del Cliente
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + dto.getIdCliente()));
            
        // 2. Validar si ya existe un detalle de persona (previene duplicados)
        if (cliente.getPersonaNatural() != null || cliente.getEmpresa() != null) {
            throw new IllegalArgumentException("El cliente ya tiene un detalle asociado (Persona Natural o Empresa) y no puede ser reasignado.");
        }

        // 3. Mapear DTO a Entidad
        PersonaNatural personaNatural = personaNaturalMapper.toEntity(dto);
        
        // 4. Establecer la relación bidireccional (CRÍTICO para JPA/Hibernate)
        personaNatural.setCliente(cliente); // Lado dueño de la FK (PersonaNatural)
        cliente.setPersonaNatural(personaNatural); // Lado inverso (Cliente)

        // 5. Guardar la entidad
        PersonaNatural savedPersona = personaNaturalRepository.save(personaNatural);
        
        // El @Transactional asegura que el estado de 'cliente' también se sincronice.

        // 6. Retornar DTO de respuesta
        return personaNaturalMapper.toResponseDto(savedPersona);
    }

    // ====================================================================
    // 2. FIND BY ID
    // ====================================================================

    @Override
    @Transactional(readOnly = true)
    public PersonaNaturalResponseDTO findById(Integer id) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Persona Natural no encontrada con ID: " + id));
        
        return personaNaturalMapper.toResponseDto(personaNatural);
    }

    // ====================================================================
    // 3. UPDATE
    // ====================================================================

    @Override
    @Transactional
    public PersonaNaturalResponseDTO update(Integer id, PersonaNaturalUpdateDTO dto) {
        
        // 1. Buscar la entidad existente
        PersonaNatural entity = personaNaturalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Persona Natural a actualizar no encontrada con ID: " + id));

        // 2. Aplicar la actualización parcial usando el Mapper
        // El Mapper se encarga de ignorar los campos 'null' del DTO (NullValuePropertyMappingStrategy.IGNORE)
        personaNaturalMapper.updateEntityFromDto(dto, entity);

        // 3. Guardar y retornar
        PersonaNatural updatedEntity = personaNaturalRepository.save(entity);
        
        return personaNaturalMapper.toResponseDto(updatedEntity);
    }
}