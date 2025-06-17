package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.ProveedorDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.entity.Proveedor;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rollos")
@Slf4j
public class RolloController {

    private final RolloService rolloService;

    public RolloController(RolloService rolloService) {
        this.rolloService = rolloService;
    }

    @GetMapping
    public ResponseEntity<List<RolloDto>> getAllRollos() {
        log.info("Obteniendo todos los rollos");
        List<RolloDto> rollos = rolloService.findAll();
        return ResponseEntity.ok(rollos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolloDto> getProveedorById(@PathVariable Long id) {
        log.info("Buscando rollo con ID: {}", id);
        return ResponseEntity.ok(rolloService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Rollo> createProveedor(@RequestBody RolloDto rollo) {
        log.info("Creando nuevo rollo: {}", rollo);
        Rollo savedRollo = rolloService.save(rollo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRollo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProveedor(@PathVariable Long id) {
        log.info("Eliminando rollo con ID: {}", id);
        if (rolloService.deleteById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}