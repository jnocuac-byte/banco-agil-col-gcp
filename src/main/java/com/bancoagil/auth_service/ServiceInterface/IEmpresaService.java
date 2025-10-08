package com.bancoagil.auth_service.ServiceInterface;

import java.util.List;

import com.bancoagil.auth_service.Dtos.EmpresaCreateDTO;
import com.bancoagil.auth_service.Dtos.EmpresaResponseDTO;
import com.bancoagil.auth_service.Dtos.EmpresaUpdateDTO;

public interface IEmpresaService {

    /**
     * Crea un registro de Empresa y lo asocia a un Cliente existente.
     * @param dto Datos para la creación.
     * @return DTO de respuesta de la Empresa creada.
     */
    EmpresaResponseDTO create(EmpresaCreateDTO dto);

    /**
     * Busca la Empresa por su ID (PK autogenerada).
     * @param id ID de la Empresa.
     * @return DTO de respuesta.
     */
    EmpresaResponseDTO findById(Integer id);

    /**
     * Actualiza la información de una Empresa existente.
     * @param id ID de la Empresa a actualizar.
     * @param dto Datos para la actualización (parcial).
     * @return DTO de respuesta con los datos actualizados.
     */
    EmpresaResponseDTO update(Integer id, EmpresaUpdateDTO dto);

    List<EmpresaResponseDTO> findAll(); 
}