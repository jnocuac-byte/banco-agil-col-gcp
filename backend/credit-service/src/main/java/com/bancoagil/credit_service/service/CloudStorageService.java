package com.bancoagil.credit_service.service;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CloudStorageService {

    @Value("${gcs.bucket-name:banco-agil-documentos}")
    private String bucketName;

    private final Storage storage;

    public CloudStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    /**
     * Sube un archivo a Google Cloud Storage
     */
    public String subirArchivo(MultipartFile file, Long idSolicitud, String tipoDocumento) throws IOException {
        // Generar nombre único
        String nombreOriginal = file.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            throw new RuntimeException("El archivo no tiene un nombre válido o extensión");
        }
        
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = "solicitud/" + idSolicitud + "/" + tipoDocumento + "_" + UUID.randomUUID() + extension;

        // Configurar metadatos
        BlobId blobId = BlobId.of(bucketName, nombreUnico);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        // Subir archivo
        storage.create(blobInfo, file.getBytes());

        // Retornar ruta en formato gs://
        return "gs://" + bucketName + "/" + nombreUnico;
    }

    /**
     * Descarga un archivo de GCS
     */
    public byte[] descargarArchivo(String rutaArchivo) throws IOException {
        String fileName = rutaArchivo.replace("gs://" + bucketName + "/", "");
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);
        
        if (blob == null) {
            throw new RuntimeException("Archivo físico no encontrado");
        }
        
        return blob.getContent();
    }

    /**
     * Elimina un archivo de GCS
     */
    public void eliminarArchivo(String rutaArchivo) {
        String fileName = rutaArchivo.replace("gs://" + bucketName + "/", "");
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
    }

    /**
     * Genera URL firmada temporal (para descarga segura)
     */
    public String obtenerUrlFirmada(String rutaArchivo, int duracionMinutos) {
        String fileName = rutaArchivo.replace("gs://" + bucketName + "/", "");
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);
        
        if (blob == null) {
            throw new RuntimeException("Archivo no encontrado");
        }

        return blob.signUrl(duracionMinutos, TimeUnit.MINUTES).toString();
    }
}