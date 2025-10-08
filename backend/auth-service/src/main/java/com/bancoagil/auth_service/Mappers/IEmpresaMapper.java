package com.bancoagil.auth_service.Mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.bancoagil.auth_service.Dtos.EmpresaCreateDTO;
import com.bancoagil.auth_service.Dtos.EmpresaResponseDTO;
import com.bancoagil.auth_service.Dtos.EmpresaUpdateDTO;
import com.bancoagil.auth_service.Entities.Empresa;

@Mapper(componentModel = "spring")
public interface IEmpresaMapper {

    // 1. Creación (DTO -> Entidad)
    // Ignoramos la PK ('id') y la relación ('cliente') que se seteará en el servicio.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    Empresa toEntity(EmpresaCreateDTO dto);

    // 2. Lectura (Entidad -> Response DTO)
    // Mapeamos los IDs desde la entidad Empresa y su relación con Cliente/Usuario.
    @Mapping(source = "cliente.id", target = "idCliente") // PK de Cliente (FK en Empresa)
    @Mapping(source = "cliente.usuario.id", target = "idUsuario") // PK de Usuario
    @Mapping(source = "cliente.usuario.email", target = "emailUsuario") // Email de Usuario
    EmpresaResponseDTO toResponseDto(Empresa entity);

    // 3. Actualización (Update DTO -> Entidad existente)
    // Usamos la estrategia IGNORE para actualizar solo los campos presentes en el DTO (actualización parcial).
    @Mapping(target = "id", ignore = true) // PK no se actualiza
    @Mapping(target = "cliente", ignore = true) // Relación no se actualiza
    @Mapping(target = "razonSocial", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "ruc", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "nombreRepresentante", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EmpresaUpdateDTO dto, @MappingTarget Empresa entity);

    List<EmpresaResponseDTO> toResponseDtoList(List<Empresa> empresas);
}