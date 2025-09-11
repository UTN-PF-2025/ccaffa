package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.CamaraDto;
import ar.utn.ccaffa.services.interfaces.AnalysisService;
import ar.utn.ccaffa.services.interfaces.CamaraService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/camaras")
@RequiredArgsConstructor
@Slf4j
public class CamaraController {

    private final CamaraService camaraService;
    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<CamaraDto> createCamara(@RequestBody CamaraDto camaraDto) {
        log.info("Creando nueva camara: {}", camaraDto.getNombre());
        CamaraDto savedCamara = camaraService.save(camaraDto);
        return new ResponseEntity<>(savedCamara, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CamaraDto> getCamaraById(@PathVariable String id) {
        log.info("Buscando camara con ID: {}", id);
        CamaraDto camaraDto = camaraService.findById(id);
        return ResponseEntity.ok(camaraDto);
    }

    @GetMapping
    public ResponseEntity<List<CamaraDto>> getAllCamaras() {
        log.info("Obteniendo todas las camaras");
        List<CamaraDto> camaras = camaraService.findAll();
        return ResponseEntity.ok(camaras);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CamaraDto> updateCamara(@PathVariable String id, @RequestBody CamaraDto camaraDto) {
        log.info("Actualizando camara con ID: {}", id);
        CamaraDto updatedCamara = camaraService.update(id, camaraDto);
        return ResponseEntity.ok(updatedCamara);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCamara(@PathVariable String id) {
        log.info("Eliminando camara con ID: {}", id);
        camaraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "cameraId", required = false) String cameraId,
            @RequestHeader(value = "X-Camera-Id", required = false) String cameraIdHeader
    ) {
        String effectiveCameraId = (cameraId != null && !cameraId.isBlank()) ? cameraId
                : (cameraIdHeader != null && !cameraIdHeader.isBlank()) ? cameraIdHeader : "cam-1";
        log.info("Recibiendo archivo: {} para enviar a análisis (cameraId={})", file.getOriginalFilename(), effectiveCameraId);
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor seleccione un archivo para subir.");
        }

        try {
            analysisService.analyzeAndNotify(file, id, effectiveCameraId);
            return ResponseEntity.ok("Archivo enviado a análisis exitosamente.");
        } catch (IOException e) {
            log.error("Error al leer el archivo para el análisis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al leer el archivo.");
        } catch (Exception e) {
            log.error("Error al procesar el archivo y enviarlo a análisis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo: " + e.getMessage());
        }
    }
}
 