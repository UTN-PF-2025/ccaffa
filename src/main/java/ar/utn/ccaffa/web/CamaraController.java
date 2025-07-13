package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.CamaraDto;
import ar.utn.ccaffa.services.interfaces.CamaraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/camaras")
@RequiredArgsConstructor
@Slf4j
public class CamaraController {

    private final CamaraService camaraService;

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
} 