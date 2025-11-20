package ar.utn.ccaffa.web;

import ar.utn.ccaffa.services.interfaces.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{cameraId}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String cameraId,
                                              @PathVariable String filename,
                                              HttpServletRequest request) {
        String relativePath = cameraId + "/" + filename;
        log.info("Solicitando imagen en ruta relativa: {}", relativePath);
        
        try {
            Resource resource = fileStorageService.loadAsResource(relativePath);
            log.info("Recurso cargado correctamente: {}", resource.getFilename());
            
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                log.debug("Tipo de contenido detectado: {}", contentType);
            } catch (IOException ex) {
                log.warn("No se pudo determinar el tipo de contenido para {}: {}", filename, ex.getMessage());
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (RuntimeException ex) {
            log.error("Error al cargar el recurso {}: {}", relativePath, ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
