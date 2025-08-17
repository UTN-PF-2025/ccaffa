package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.*;
import ar.utn.ccaffa.services.interfaces.RolloProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rollos_productos")
@Slf4j
public class RolloProductoController {

    private final RolloProductoService rolloService;

    public RolloProductoController(RolloProductoService rolloService) {
        this.rolloService = rolloService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolloProductoDto> getRollosById(@PathVariable Long id) {
        log.info("Buscando rollo con ID: {}", id);
        return ResponseEntity.ok(rolloService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RolloProductoDto>> getAllRollos(FiltroRolloProductoDto filtros) {
        log.info("Obteniendo todos los rollos productos");
        List<RolloProductoDto> rollos = rolloService.filtrarRollosProducto(filtros);
        return ResponseEntity.ok(rollos);
    }


    @PostMapping
    public ResponseEntity<RolloProductoDto> createRollo(@RequestBody RolloProductoDto rollo) {
        log.info("Creando nuevo producto: {}", rollo);
        RolloProductoDto savedRollo = rolloService.save(rollo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRollo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolloProductoDto> updateRollo(@PathVariable Long id, @RequestBody RolloProductoDto rollo) {
        rollo.setId(id);
        return ResponseEntity.ok(rolloService.save(rollo));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRollo(@PathVariable Long id) {
        log.info("Eliminando rollo con ID: {}", id);
        if (rolloService.deleteById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/obtenerProductosGeneradosDeRollo/{id}")
    public ResponseEntity<List<RolloProductoDto>> obtenerProductosGeneradosDeRolloID(@PathVariable Long id) {
        List<RolloProductoDto> rollos = rolloService.findByRolloPadreId(id);
        return ResponseEntity.ok(rollos);

    }

    @GetMapping("/obtenerProductosGeneradosPorOrdenDeTrabajo/{id}")
    public ResponseEntity<List<RolloProductoDto>> obtenerProductosGeneradosPorOrdenDeTrabajoID(@PathVariable Long id) {
        List<RolloProductoDto> rollos = rolloService.findByOrdenDeTrabajoId(id);
        return ResponseEntity.ok(rollos);

    }

}