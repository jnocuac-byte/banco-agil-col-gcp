package com.bancoagil.auth_service.Mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bancoagil.auth_service.Dtos.UsuarioCreateDTO;
import com.bancoagil.auth_service.Dtos.UsuarioResponseDTO;
import com.bancoagil.auth_service.Dtos.UsuarioUpdateDTO;
import com.bancoagil.auth_service.Entities.Usuario;

/**
 * Interfaz de mapeo para la conversión de objetos Usuario <-> DTOs.
 * MapStruct genera automáticamente la implementación en tiempo de compilación.
 */
@Mapper(componentModel = "spring") // Le dice a MapStruct que genere la implementación como un Bean de Spring
public interface IUsuarioMapper {

    // 1. DTO de Creación a Entity
    // Omitimos el campo 'id' y asignamos valores por defecto para 'activo', 
    // 'fechaCreacion' y 'fechaActualizacion' se manejarán en el Service.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Usuario toEntity(UsuarioCreateDTO dto);

    // 2. Entity a DTO de Respuesta
    // No necesitamos @Mapping si los campos se llaman igual. 
    // El campo 'password' de la Entity es ignorado automáticamente por el DTO de respuesta.
    UsuarioResponseDTO toResponseDto(Usuario entity);

    // 3. DTO de Actualización a Entity Existente
    // Este método solo mapea los campos que existen en el DTO de actualización 
    // y los aplica sobre la Entity existente (@MappingTarget).
    @Mapping(target = "id", ignore = true) // No se actualiza el ID
    @Mapping(target = "password", ignore = true) // No se actualiza el password directamente
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "email", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "activo", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UsuarioUpdateDTO dto, @MappingTarget Usuario entity);

    // Opcional: Mapear lista de Entities a lista de DTOs
    List<UsuarioResponseDTO> toResponseDtoList(List<Usuario> entities);
}