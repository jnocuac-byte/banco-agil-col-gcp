package com.bancoagil.auth_service.ServiceInterface;

import java.util.List;

import com.bancoagil.auth_service.Dtos.ClienteCreateDTO;
import com.bancoagil.auth_service.Dtos.ClienteResponseDTO;
import com.bancoagil.auth_service.Dtos.ClienteUpdateDTO;

public interface IClienteService {
    ClienteResponseDTO createCliente(ClienteCreateDTO clienteDto);
    ClienteResponseDTO getClienteById(Integer id);
    List<ClienteResponseDTO> getAllClientes();
    ClienteResponseDTO updateCliente(Integer id, ClienteUpdateDTO clienteDto);
    void deleteCliente(Integer id);
}