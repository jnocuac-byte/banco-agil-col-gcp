package com.bancoagil.auth_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancoagil.auth_service.dto.LoginDTO;
import com.bancoagil.auth_service.dto.LoginResponseDTO;
import com.bancoagil.auth_service.dto.RegistroClienteDTO;
import com.bancoagil.auth_service.model.Usuario;
import com.bancoagil.auth_service.service.AuthService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5500", "https://127.0.0.1:5500", "http://localhost:3000"})
public class AuthController {
    
    @Autowired
    private AuthService authService;

    // Endpoint para registrar cliente
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrarCliente(@Valid @RequestBody RegistroClienteDTO dto){
        Map<String, Object> response = new HashMap<>();

        try{
            Usuario usuario = authService.registrarCliente(dto);

            response.put("success", true);
            response.put("message", "Cliente registrado exitosamente");
            response.put("usuarioId", usuario.getId());
            response.put("email", usuario.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        }catch(Exception e){
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    // Endpoint para logear cliente
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto){
        try {
            LoginResponseDTO response = authService.login(dto);  
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO(
                false,
                e.getMessage(),
                null, null, null, null, null, null, null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/backoffice/login")
    public ResponseEntity<LoginResponseDTO> loginAsesor(@Valid @RequestBody LoginDTO dto) {
        try {
            LoginResponseDTO response = authService.loginAsesor(dto);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO(
                false,
                e.getMessage(),
                null, null, null, null, null, null, null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // Endpoint para health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("service", "auth-service");
        response.put("message", "Health check successful");
        return ResponseEntity.ok(response);
    }
    
}
