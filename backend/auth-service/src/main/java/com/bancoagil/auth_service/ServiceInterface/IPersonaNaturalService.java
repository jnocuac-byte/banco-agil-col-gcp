package com.bancoagil.auth_service.ServiceInterface;

import com.bancoagil.auth_service.Dtos.PersonaNaturalCreateDTO;
import com.bancoagil.auth_service.Dtos.PersonaNaturalResponseDTO;
import com.bancoagil.auth_service.Dtos.PersonaNaturalUpdateDTO;

public interface IPersonaNaturalService {

    /**
     * Crea un registro de PersonaNatural y lo asocia a un Cliente existente.
     * @param dto Datos para la creación.
     * @return DTO de respuesta de la Persona Natural creada.
     */
    PersonaNaturalResponseDTO create(PersonaNaturalCreateDTO dto);

    /**
     * Busca la PersonaNatural por su ID (PK autogenerada).
     * @param id ID de la PersonaNatural.
     * @return DTO de respuesta.
     */
    PersonaNaturalResponseDTO findById(Integer id);

    /**
     * Actualiza la información de una PersonaNatural existente.
     * @param id ID de la PersonaNatural a actualizar.
     * @param dto Datos para la actualización (parcial).
     * @return DTO de respuesta con los datos actualizados.
     */
    PersonaNaturalResponseDTO update(Integer id, PersonaNaturalUpdateDTO dto);
}
