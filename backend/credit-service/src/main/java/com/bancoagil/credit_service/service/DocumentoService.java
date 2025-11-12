package com.bancoagil.credit_service.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bancoagil.credit_service.model.Documento;
import com.bancoagil.credit_service.model.SolicitudCredito;
import com.bancoagil.credit_service.repository.DocumentoRepository;
import com.bancoagil.credit_service.repository.SolicitudCreditoRepository;

// Servicio para gestionar documentos relacionados con solicitudes de crédito
@Service
public class DocumentoService {

    // Inyección de los repositorios necesarios
    @Autowired
    private DocumentoRepository documentoRepository;

    // Inyección del repositorio de solicitudes para validar la existencia de la solicitud
    @Autowired
    private SolicitudCreditoRepository solicitudRepository;

    @Autowired
    private CloudStorageService cloudStorageService;

    // Directorio base para almacenar los documentos (configurable en application.properties)
    @Value("${app.upload.dir:uploads/documentos}") // Valor por defecto si no está configurado
    private String uploadDir;

    // Método para subir un documento asociado a una solicitud de crédito
    @Transactional
    public Documento subirDocumento(Long idSolicitud, String tipoDocumento, MultipartFile file) throws IOException {

        // Validar que la solicitud existe
        @SuppressWarnings("unused")
        SolicitudCredito solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Validar el archivo
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        // Validar tamaño máximo (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("El archivo excede el tamaño máximo de 10MB");
        }

        // Validar formato (PDF, imágenes, Excel)
        String contentType = file.getContentType();
        if (!esFormatoPermitido(contentType)) {
            throw new RuntimeException("Formato de archivo no permitido. Use PDF, imágenes o Excel");
        }

        // ===== USAR GCS EN LUGAR DEL SISTEMA DE ARCHIVOS =====
        String rutaArchivo = cloudStorageService.subirArchivo(file, idSolicitud, tipoDocumento);
        String nombreOriginal = file.getOriginalFilename();
        // ===== FIN DEL CAMBIO =====

        // Crear y guardar el registro del documento en la base de datos
        Documento documento = new Documento();
        documento.setIdSolicitud(idSolicitud);
        documento.setTipoDocumento(tipoDocumento);
        documento.setNombreArchivo(nombreOriginal);
        documento.setRutaArchivo(rutaArchivo); // Ahora guarda "gs://bucket/path"
        documento.setTamanoBytes(file.getSize());

        return documentoRepository.save(documento);
    }
    // Listar documentos asociados a una solicitud de crédito
    @Transactional(readOnly = true)
    // Listar documentos por ID de solicitud
    public List<Documento> listarDocumentosSolicitud(Long idSolicitud) {
        return documentoRepository.findByIdSolicitud(idSolicitud); // Listar documentos por ID de solicitud
    }

    // Obtener un documento por su ID
    @Transactional(readOnly = true)
    // Obtener documento por ID
    public Documento obtenerDocumento(Long id) {
        return documentoRepository.findById(id) // Obtener documento por ID
                .orElseThrow(() -> new RuntimeException("Documento no encontrado")); // Lanzar error si no se encuentra
    }

    // Eliminar un documento por su ID
    @Transactional
    public void eliminarDocumento(Long id) throws IOException {
        Documento documento = obtenerDocumento(id);
        
        // ===== CAMBIO: ELIMINAR DESDE GCS =====
        cloudStorageService.eliminarArchivo(documento.getRutaArchivo());
        // ===== FIN DEL CAMBIO =====
        
        // Eliminar registro de BD
        documentoRepository.delete(documento);
    }

    // Descargar el contenido de un documento por su ID
    public byte[] descargarDocumento(Long id) throws IOException {
        Documento documento = obtenerDocumento(id);
        
        // ===== CAMBIO: DESCARGAR DESDE GCS =====
        return cloudStorageService.descargarArchivo(documento.getRutaArchivo());
        // ===== FIN DEL CAMBIO =====
    }

    // Método auxiliar para validar formatos permitidos
    private boolean esFormatoPermitido(String contentType) {
        // Validar tipos MIME comunes para PDF, imágenes y Excel
        return contentType != null && (
            contentType.equals("application/pdf") || // PDF
            contentType.startsWith("image/") || // Imágenes (jpeg, png, gif, etc.)
            contentType.equals("application/vnd.ms-excel") || // Excel .xls
            contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") // Excel .xlsx
        );
    }
}
