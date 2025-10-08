package com.bancoagil.auth_service.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bancoagil.auth_service.Dtos.EmpresaCreateDTO;
import com.bancoagil.auth_service.Dtos.EmpresaResponseDTO;
import com.bancoagil.auth_service.Dtos.EmpresaUpdateDTO;
import com.bancoagil.auth_service.Entities.Cliente;
import com.bancoagil.auth_service.Entities.Empresa;
import com.bancoagil.auth_service.Exceptions.ResourceNotFoundException;
import com.bancoagil.auth_service.Mappers.IEmpresaMapper;
import com.bancoagil.auth_service.Repository.IClienteRepository;
import com.bancoagil.auth_service.Repository.IEmpresaRepository;
import com.bancoagil.auth_service.ServiceInterface.IEmpresaService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor // Inyección para campos 'final'
public class EmpresaServiceImpl implements IEmpresaService {

    private final IEmpresaRepository empresaRepository;
    private final IClienteRepository clienteRepository;
    private final IEmpresaMapper empresaMapper;

    // ====================================================================
    // 1. CREATE
    // ====================================================================
    @Override
    @Transactional
    public EmpresaResponseDTO create(EmpresaCreateDTO dto) {
        
        // 1. Buscar y validar la existencia del Cliente
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + dto.getIdCliente()));
            
        // 2. Validar exclusividad (El cliente NO debe tener PersonaNatural NI Empresa)
        if (cliente.getPersonaNatural() != null || cliente.getEmpresa() != null) {
            throw new IllegalArgumentException("El cliente ya tiene un detalle asociado (Persona Natural o Empresa).");
        }

        // 3. Mapear DTO a Entidad
        Empresa empresa = empresaMapper.toEntity(dto);
        
        // 4. Establecer la relación bidireccional (CRÍTICO)
        empresa.setCliente(cliente);      // Lado dueño de la FK (Empresa)
        cliente.setEmpresa(empresa);      // Lado inverso (Cliente)

        // 5. Guardar la entidad
        Empresa savedEmpresa = empresaRepository.save(empresa);

        // 6. Retornar DTO de respuesta
        return empresaMapper.toResponseDto(savedEmpresa);
    }

    // ====================================================================
    // 2. FIND BY ID
    // ====================================================================
    @Override
    @Transactional(readOnly = true)
    public EmpresaResponseDTO findById(Integer id) {
        Empresa empresa = empresaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        
        return empresaMapper.toResponseDto(empresa);
    }

    // ====================================================================
    // 3. UPDATE
    // ====================================================================
    @Override
    @Transactional
    public EmpresaResponseDTO update(Integer id, EmpresaUpdateDTO dto) {
        
        // 1. Buscar la entidad existente
        Empresa entity = empresaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa a actualizar no encontrada con ID: " + id));

        // 2. Aplicar la actualización parcial usando el Mapper
        empresaMapper.updateEntityFromDto(dto, entity);

        // 3. Guardar y retornar
        Empresa updatedEntity = empresaRepository.save(entity);
        
        return empresaMapper.toResponseDto(updatedEntity);
    }


    @Override
    @Transactional(readOnly = true)
    public List<EmpresaResponseDTO> findAll() {
        
        // 1. Obtiene todas las entidades
        List<Empresa> empresas = empresaRepository.findAll();
        
        // 2. Mapea la lista de entidades a la lista de DTOs de respuesta
        // Nota: Asegúrate de añadir el método toResponseDtoList en IEmpresaMapper (ver abajo)
        return empresaMapper.toResponseDtoList(empresas);
    }
}