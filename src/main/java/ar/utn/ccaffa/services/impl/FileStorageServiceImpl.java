package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.services.interfaces.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.storage.path:./storage/images}")
    private String storagePath;

    private Path rootLocation;

    @PostConstruct
    public void init() throws IOException {
        this.rootLocation = Paths.get(storagePath).toAbsolutePath().normalize();
        if (Files.notExists(rootLocation)) {
            Files.createDirectories(rootLocation);
            log.info("Directorio de almacenamiento creado en: {}", this.rootLocation);
        }
    }

    @Override
    public String save(byte[] fileBytes, String originalFilename) throws IOException {
        // Limpiar el nombre del archivo para eliminar secuencias de ruta (ej. "../")
        String cleanFilename = StringUtils.cleanPath(originalFilename);

        // Generar un nombre de archivo único para evitar colisiones
        if (cleanFilename.contains("..")) {
            // Comprobación de seguridad explícita
            throw new IOException("No se puede guardar un archivo con una ruta relativa fuera del directorio actual: " + cleanFilename);
        }

        String uniqueFilename = UUID.randomUUID().toString() + "_" + cleanFilename;
        Path destinationFile = this.rootLocation.resolve(uniqueFilename);

        // Validación de seguridad más robusta
        if (!destinationFile.startsWith(this.rootLocation)) {
            throw new IOException("No se puede guardar el archivo fuera del directorio raíz: " + cleanFilename);
        }

        Files.write(destinationFile, fileBytes);
        log.info("Archivo guardado en: {}", destinationFile);

        return uniqueFilename;
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer el archivo: " + filename, e);
        }
    }
}
