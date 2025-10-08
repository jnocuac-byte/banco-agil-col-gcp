package com.bancoagil.account_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public Map<String, String> test() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "¡Microservicio de Cuentas y Transacciones funcionando correctamente!");
        response.put("service", "account-service");
        response.put("port", "8082");
        response.put("endpoints", "Disponibles: /api/cuentas, /api/transacciones");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "account-service");
        return response;
    }
}
