package com.bancoagil.auth_service.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.bancoagil.auth_service.Dtos.PersonaNaturalCreateDTO;
import com.bancoagil.auth_service.Dtos.PersonaNaturalResponseDTO;
import com.bancoagil.auth_service.Dtos.PersonaNaturalUpdateDTO;
import com.bancoagil.auth_service.Entities.PersonaNatural;

@Mapper(componentModel = "spring")
public interface IPersonaNaturalMapper {

    // 1. Creación (DTO -> Entidad)
    // Ignoramos la PK autogenerada ('id') y la relación ('cliente') que se seteará en el servicio.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    PersonaNatural toEntity(PersonaNaturalCreateDTO dto);

    // 2. Lectura (Entidad -> Response DTO)
    // Mapeamos los IDs desde la entidad PersonaNatural y su relación con Cliente/Usuario.
    @Mapping(source = "cliente.id", target = "idCliente") // PK de Cliente (FK en PersonaNatural)
    @Mapping(source = "cliente.usuario.id", target = "idUsuario") // PK de Usuario
    @Mapping(source = "cliente.usuario.email", target = "emailUsuario") // Email de Usuario
    PersonaNaturalResponseDTO toResponseDto(PersonaNatural entity);

    // 3. Actualización (Update DTO -> Entidad existente)
    // Usamos la estrategia IGNORE para actualizar solo los campos presentes en el DTO (actualización parcial).
    @Mapping(target = "id", ignore = true) // PK no se actualiza
    @Mapping(target = "cliente", ignore = true) // Relación no se actualiza
    @Mapping(target = "nombre", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "apellido", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "identificacion", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "fechaNacimiento", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PersonaNaturalUpdateDTO dto, @MappingTarget PersonaNatural entity);
}