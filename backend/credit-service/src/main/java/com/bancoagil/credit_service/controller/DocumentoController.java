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

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @PostMapping("/solicitud/{idSolicitud}/subir")
    public ResponseEntity<Map<String, Object>> subirDocumento(
            @PathVariable Long idSolicitud,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Documento documento = documentoService.subirDocumento(idSolicitud, tipoDocumento, file);
            
            response.put("success", true);
            response.put("message", "Documento subido exitosamente");
            response.put("documentoId", documento.getId());
            response.put("nombreArchivo", documento.getNombreArchivo());
            response.put("tamanoBytes", documento.getTamanoBytes());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error al guardar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<Documento>> listarDocumentos(@PathVariable Long idSolicitud) {
        List<Documento> documentos = documentoService.listarDocumentosSolicitud(idSolicitud);
        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargarDocumento(@PathVariable Long id) {
        try {
            Documento documento = documentoService.obtenerDocumento(id);
            byte[] contenido = documentoService.descargarDocumento(id);
            
            // Determinar el tipo MIME basado en la extensión
            String contentType = determinarContentType(documento.getNombreArchivo());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + documento.getNombreArchivo() + "\"")
                    .body(contenido);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarDocumento(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            documentoService.eliminarDocumento(id);
            response.put("success", true);
            response.put("message", "Documento eliminado exitosamente");
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error al eliminar el archivo físico");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private String determinarContentType(String nombreArchivo) {
        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
        
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> "application/octet-stream";
        };
    }
}