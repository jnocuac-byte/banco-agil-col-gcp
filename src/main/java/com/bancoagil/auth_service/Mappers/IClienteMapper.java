package com.bancoagil.auth_service.Mappers;

import com.bancoagil.auth_service.Dtos.ClienteCreateDTO;
import com.bancoagil.auth_service.Dtos.ClienteResponseDTO;
import com.bancoagil.auth_service.Dtos.ClienteUpdateDTO;
import com.bancoagil.auth_service.Entities.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IClienteMapper {

    // --- 1. DTO de Creación a Entidad (POST) ---
    // Ignoramos la nueva PK (ID) y la relación (Usuario).
    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "usuario", ignore = true) 
    Cliente toClienteEntity(ClienteCreateDTO dto);

    // --- 2. Entidad a DTO de Respuesta (GET) ---
    @Mapping(source = "id", target = "idCliente") // Mapea la nueva PK de Cliente
    @Mapping(source = "usuario.id", target = "idUsuario") // Mapea la FK (ID de Usuario)
    @Mapping(source = "usuario.email", target = "emailUsuario") // Campo extra de Usuario
    ClienteResponseDTO toResponseDto(Cliente cliente);
    
    List<ClienteResponseDTO> toResponseDtoList(List<Cliente> clientes);

    // --- 3. DTO de Actualización a Entidad existente (PUT) ---
    // Mapeamos los campos que queremos actualizar, ignorando nulos.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    
    // Si la propiedad existe en el DTO, la mapeamos. Si es nula, se ignora.
    @Mapping(target = "tipoCliente", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "telefono", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "direccion", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "ciudad", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ClienteUpdateDTO dto, @MappingTarget Cliente entity);
}