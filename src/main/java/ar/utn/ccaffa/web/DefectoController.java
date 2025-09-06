package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.DefectoDto;
import ar.utn.ccaffa.services.interfaces.DefectoService;
import ar.utn.ccaffa.services.interfaces.FileStorageService;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/defectos")
@RequiredArgsConstructor
@Slf4j
public class DefectoController {

    private final DefectoService defectoService;
    private final FileStorageService fileStorageService;

    @GetMapping("/{id}")
    public ResponseEntity<DefectoDto> getDefectoById(@PathVariable Long id) {
        log.info("Buscando defecto con ID: {}", id);
        DefectoDto defectoDto = defectoService.findById(id);
        return ResponseEntity.ok(defectoDto);
    }

    @GetMapping("/imagenes/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        log.info("Buscando imagen: {}", filename);
        Resource file = fileStorageService.loadAsResource(filename);
        return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PutMapping("/{imageId:.+}/rechazar")
    public ResponseEntity<DefectoDto> rechazarDefecto(@PathVariable("imageId") String imageId) {
        log.info("Rechazando defecto con imageId: {}", imageId);
        DefectoDto actualizado = defectoService.actualizarEstadoRechazoPorImagen(imageId, true);
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/{imageId:.+}/aceptar")
    public ResponseEntity<DefectoDto> aceptarDefecto(@PathVariable("imageId") String imageId) {
        log.info("Aceptando defecto con imageId: {}", imageId);
        DefectoDto actualizado = defectoService.actualizarEstadoRechazoPorImagen(imageId, false);
        return ResponseEntity.ok(actualizado);
    }
}

