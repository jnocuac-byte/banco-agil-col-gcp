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
import com.bancoagil.auth_service.model.Cliente;
import com.bancoagil.auth_service.model.Cuenta;
import com.bancoagil.auth_service.model.Usuario;
import com.bancoagil.auth_service.repository.ClienteRepository;
import com.bancoagil.auth_service.repository.CuentaRepository;
import com.bancoagil.auth_service.service.AuthService;

import jakarta.validation.Valid;

// Controlador REST para autenticación y registro
@RestController
@RequestMapping("/api/auth") // Ruta base para el controlador
@CrossOrigin(origins = {"http://localhost:5500", "https://127.0.0.1:5500", "http://localhost:3000"})
public class AuthController {
    
    // Inyección del servicio de autenticación
    @Autowired
    private AuthService authService;

    // Inyección de los repositorios necesarios
    @Autowired
    private ClienteRepository clienteRepository;

    // Inyección del repositorio de cuentas
    @Autowired
    private CuentaRepository cuentaRepository;

    // Endpoint para registrar cliente
    @PostMapping("/registro")
    // Registrar un nuevo cliente
    public ResponseEntity<Map<String, Object>> registrarCliente(@Valid @RequestBody RegistroClienteDTO dto){
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();

        // Intentar registrar el cliente
        try{
            // Llamar al servicio para registrar el cliente
            Usuario usuario = authService.registrarCliente(dto);
            
            // Obtener la cuenta creada
            Cliente cliente = clienteRepository.findByIdUsuario(usuario.getId()).orElse(null);
            String numeroCuenta = null;
            
            // Buscar la cuenta asociada al cliente
            if (cliente != null) {
                // Buscar la cuenta asociada al cliente
                Cuenta cuentaEncontrada = cuentaRepository.findByIdCliente(cliente.getId())
                    .stream() // Convertir a stream
                    .findFirst() // Obtener el primer elemento
                    .orElse(null); // Si no hay cuenta, devolver null
                
                // Si se encontró una cuenta, obtener su número
                if (cuentaEncontrada != null) {
                    numeroCuenta = cuentaEncontrada.getNumeroCuenta(); // Obtener el número de cuenta
                }
            }

            // Preparar respuesta exitosa
            response.put("success", true);
            response.put("message", "Cliente registrado exitosamente");
            response.put("usuarioId", usuario.getId());
            response.put("email", usuario.getEmail());
            response.put("numeroCuenta", numeroCuenta); // ← NUEVO

            // Devolver respuesta con estado 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        }catch(Exception e){ // Manejar errores
            // Preparar respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());

            // Devolver respuesta con estado 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
}

    // Endpoint para logear cliente
    @PostMapping("/login")
    // Autenticar un cliente y devolver token JWT
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto){
        // Mapa para la respuesta
        try {
            // Llamar al servicio para autenticar y obtener el token
            LoginResponseDTO response = authService.login(dto);  
            return ResponseEntity.ok(response);
        } catch (Exception e) { // Manejar errores
            // Preparar respuesta de error
            LoginResponseDTO errorResponse = new LoginResponseDTO( //<- Nuevo objeto de respuesta
                false, // Indicador de éxito
                e.getMessage(), // Mensaje de error
                null, null, null, null, null, null, null // Todos los demás campos como null
            );

            // Devolver respuesta con estado 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // Endpoint para logear asesor (backoffice)
    @PostMapping("/backoffice/login")
    // Autenticar un asesor y devolver token JWT
    public ResponseEntity<LoginResponseDTO> loginAsesor(@Valid @RequestBody LoginDTO dto) {
        // Mapa para la respuesta
        try {
            // Llamar al servicio para autenticar y obtener el token
            LoginResponseDTO response = authService.loginAsesor(dto);
            return ResponseEntity.ok(response); // Devolver respuesta con estado 200 OK
            
        } catch (RuntimeException e) { // Manejar errores
            // Preparar respuesta de error
            LoginResponseDTO errorResponse = new LoginResponseDTO(
                false, // Indicador de éxito
                e.getMessage(), // Mensaje de error
                null, null, null, null, null, null, null // Todos los demás campos como null
            );

            // Devolver respuesta con estado 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // Endpoint para health check
    @GetMapping("/health")
    // Verificar el estado del servicio
    public ResponseEntity<Map<String, String>> health() {
        // Mapa para la respuesta
        Map<String, String> response = new HashMap<>();
        // Preparar respuesta de salud
        response.put("status", "success");
        response.put("service", "auth-service");
        response.put("message", "Health check successful");
        // Devolver respuesta con estado 200 OK
        return ResponseEntity.ok(response);
    }
    
    /*
    @GetMapping("/setup-asesor")
    public ResponseEntity<String> setupAsesor() {
        authService.crearAsesorInicial();
        return ResponseEntity.ok("Asesor creado/actualizado con contraseña encriptada");
    }*/
}
