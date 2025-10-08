package com.bancoagil.credit_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bancoagil.credit_service.model.Documento;
import com.bancoagil.credit_service.model.SolicitudCredito;
import com.bancoagil.credit_service.repository.DocumentoRepository;
import com.bancoagil.credit_service.repository.SolicitudCreditoRepository;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private SolicitudCreditoRepository solicitudRepository;

    @Value("${app.upload.dir:uploads/documentos}")
    private String uploadDir;

    @Transactional
    public Documento subirDocumento(Long idSolicitud, String tipoDocumento, MultipartFile file) throws IOException {

        @SuppressWarnings("unused")
        SolicitudCredito solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("El archivo excede el tamaño máximo de 10MB");
        }

        String contentType = file.getContentType();
        if (!esFormatoPermitido(contentType)) {
            throw new RuntimeException("Formato de archivo no permitido. Use PDF, imágenes o Excel");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String nombreOriginal = file.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            throw new RuntimeException("El archivo no tiene un nombre válido o extensión");
        }
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = UUID.randomUUID().toString() + extension;

        Path rutaArchivo = uploadPath.resolve(nombreUnico);
        Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        Documento documento = new Documento();
        documento.setIdSolicitud(idSolicitud);
        documento.setTipoDocumento(tipoDocumento);
        documento.setNombreArchivo(nombreOriginal);
        documento.setRutaArchivo(rutaArchivo.toString());
        documento.setTamanoBytes(file.getSize());

        return documentoRepository.save(documento);
    }

    @Transactional(readOnly = true)
    public List<Documento> listarDocumentosSolicitud(Long idSolicitud) {
        return documentoRepository.findByIdSolicitud(idSolicitud);
    }

    @Transactional(readOnly = true)
    public Documento obtenerDocumento(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
    }

    @Transactional
    public void eliminarDocumento(Long id) throws IOException {
        Documento documento = obtenerDocumento(id);
        
        // Eliminar archivo físico
        Path rutaArchivo = Paths.get(documento.getRutaArchivo());
        Files.deleteIfExists(rutaArchivo);
        
        // Eliminar registro de BD
        documentoRepository.delete(documento);
    }

    public byte[] descargarDocumento(Long id) throws IOException {
        Documento documento = obtenerDocumento(id);
        Path rutaArchivo = Paths.get(documento.getRutaArchivo());
        
        if (!Files.exists(rutaArchivo)) {
            throw new RuntimeException("Archivo físico no encontrado");
        }
        
        return Files.readAllBytes(rutaArchivo);
    }

    private boolean esFormatoPermitido(String contentType) {
        return contentType != null && (
            contentType.equals("application/pdf") ||
            contentType.startsWith("image/") ||
            contentType.equals("application/vnd.ms-excel") ||
            contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );
    }
}
