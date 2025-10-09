package com.bancoagil.credit_service.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bancoagil.credit_service.model.Documento;
import com.bancoagil.credit_service.service.DocumentoService;

// Controlador para gestionar documentos relacionados con solicitudes de crédito
@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    // Inyección del servicio de documentos
    @Autowired
    private DocumentoService documentoService;

    // Endpoint para subir un documento asociado a una solicitud de crédito
    @PostMapping("/solicitud/{idSolicitud}/subir")
    public ResponseEntity<Map<String, Object>> subirDocumento(
            @PathVariable Long idSolicitud,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("file") MultipartFile file) { // Recibe el ID de la solicitud, el tipo de documento y el archivo
        
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar subir el documento
        try {
            // Llamar al servicio para subir el documento
            Documento documento = documentoService.subirDocumento(idSolicitud, tipoDocumento, file);
            
            // Preparar la respuesta exitosa
            response.put("success", true);
            response.put("message", "Documento subido exitosamente");
            response.put("documentoId", documento.getId());
            response.put("nombreArchivo", documento.getNombreArchivo());
            response.put("tamanoBytes", documento.getTamanoBytes());
            
            // Retornar la respuesta con estado 201 (CREATED)
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IOException e) { // Manejar errores de IO
            // Preparar la respuesta de error
            response.put("success", false);
            response.put("message", "Error al guardar el archivo: " + e.getMessage());

            // Retornar la respuesta con estado 500 (INTERNAL SERVER ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); 
        } catch (RuntimeException e) { // Manejar otros errores (como solicitud no encontrada)
            // Preparar la respuesta de error
            response.put("success", false);
            response.put("message", e.getMessage());

            // Retornar la respuesta con estado 400 (BAD REQUEST)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Endpoint para listar documentos asociados a una solicitud de crédito
    @GetMapping("/solicitud/{idSolicitud}")
    // Listar documentos por ID de solicitud
    public ResponseEntity<List<Documento>> listarDocumentos(@PathVariable Long idSolicitud) {
        // Llamar al servicio para obtener la lista de documentos
        List<Documento> documentos = documentoService.listarDocumentosSolicitud(idSolicitud);

        // Retornar la lista de documentos con estado 200 (OK)
        return ResponseEntity.ok(documentos);
    }

    // Endpoint para descargar un documento por su ID
    @GetMapping("/{id}/descargar")
    // Descargar documento por ID
    public ResponseEntity<byte[]> descargarDocumento(@PathVariable Long id) {
        // Intentar obtener y descargar el documento
        try {
            // Obtener el documento para obtener metadatos
            Documento documento = documentoService.obtenerDocumento(id);
            byte[] contenido = documentoService.descargarDocumento(id);
            
            // Determinar el tipo MIME basado en la extensión
            String contentType = determinarContentType(documento.getNombreArchivo());
            
            // Retornar el archivo con los encabezados adecuados
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // Tipo MIME
                    .header(HttpHeaders.CONTENT_DISPOSITION, // Forzar descarga con nombre original
                            "attachment; filename=\"" + documento.getNombreArchivo() + "\"") // Nombre del archivo
                    .body(contenido); // Contenido del archivo
                    
        } catch (IOException e) { // Manejar errores de IO
             // En caso de error, retornar estado 500 (INTERNAL SERVER ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) { // Manejar caso de documento no encontrado
            // Retornar estado 404 (NOT FOUND) si el documento no existe
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para eliminar un documento por su ID
    @DeleteMapping("/{id}")
    // Eliminar documento por ID
    public ResponseEntity<Map<String, Object>> eliminarDocumento(@PathVariable Long id) {
        // Mapa para la respuesta
        Map<String, Object> response = new HashMap<>();
        
        // Intentar eliminar el documento
        try {
            // Llamar al servicio para eliminar el documento
            documentoService.eliminarDocumento(id); // Elimina tanto la base de datos como el archivo físico
            response.put("success", true); // Indica éxito
            response.put("message", "Documento eliminado exitosamente"); // Mensaje de éxito

            // Retornar la respuesta con estado 200 (OK)
            return ResponseEntity.ok(response);
            
        } catch (IOException e) { // Manejar errores de IO
             // Preparar la respuesta de error
            response.put("success", false); // Indica fallo
            response.put("message", "Error al eliminar el archivo físico"); // Mensaje de error

            // Retornar la respuesta con estado 500 (INTERNAL SERVER ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (RuntimeException e) { // Manejar caso de documento no encontrado
             // Preparar la respuesta de error
            response.put("success", false); // Indica fallo
            response.put("message", e.getMessage()); // Mensaje de error

            // Retornar la respuesta con estado 404 (NOT FOUND)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Método auxiliar para determinar el tipo MIME basado en la extensión del archivo
    private String determinarContentType(String nombreArchivo) {
        // Obtener la extensión del archivo
        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
        
        // Mapear extensiones comunes a tipos MIME
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> "application/octet-stream";
        }; // Tipo genérico si no se reconoce la extensión
    }
}