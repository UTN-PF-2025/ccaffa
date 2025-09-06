package ar.utn.ccaffa.web;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.TipoMaterial;
import ar.utn.ccaffa.model.dto.FiltroRolloDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.EnumSet;
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
    public ResponseEntity<List<RolloDto>> getAllRollos(FiltroRolloDto filtros) {
        log.info("Obteniendo todos los rollos");
        List<RolloDto> rollos = rolloService.filtrarRollos(filtros);
        return ResponseEntity.ok(rollos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RolloDto> getRollosById(@PathVariable Long id) {
        log.info("Buscando rollo con ID: {}", id);
        return ResponseEntity.ok(rolloService.findById(id));
    }

    @GetMapping("/tipoMateriales")
    public ResponseEntity<EnumSet<TipoMaterial>> getRollosById() {
        return ResponseEntity.ok(EnumSet.allOf(TipoMaterial.class));
    }
    @GetMapping("/{id}/conRollosPadres")
    public ResponseEntity<RolloDto> getRolloByIdConRollosPadres(@PathVariable Long id) {
        log.info("Buscando rollo con ID: {}", id);
        return ResponseEntity.ok(rolloService.findByIdConRollosPadres(id));
    }

    @GetMapping("/{id}/arbolDeRollosHijos")
    public ResponseEntity<RolloDto> getRolloByIdConArbolDeRollosHijos(@PathVariable Long id) {
        log.info("Buscando rollo con ID: {}", id);
        return ResponseEntity.ok(rolloService.obtenerArbolCompletoDeRollosHijos(id));
    }

    @GetMapping("/{id}/rollosDisponibles")
    public ResponseEntity<List<RolloDto>> getRollosDisponibles(@PathVariable Long id) {
        log.info("Buscando rollos disponibles para la orden de venta con ID: {}", id);
        return ResponseEntity.ok(rolloService.obtenerRollosDisponiblesParaOrdenVenta(id));
    }

    @PostMapping
    public ResponseEntity<RolloDto> createRollo(@RequestBody RolloDto rollo) {
        log.info("Creando nuevo rollo: {}", rollo);
        rollo.setEstado(EstadoRollo.DISPONIBLE);
        rollo.setFechaIngreso(LocalDateTime.now());
        RolloDto savedRollo = rolloService.save(rollo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRollo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolloDto> updateRollo(@PathVariable Long id, @RequestBody RolloDto rollo) {
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

    @PostMapping("/{id}/anular")
    public ResponseEntity<Void> anularRollo(@PathVariable Long id) {
        log.info("Anulando rollo con ID: {}", id);
        if (rolloService.anularRollo(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}