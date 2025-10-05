/*package ar.utn.ccaffa.web;

import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.model.dto.*;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;
import ar.utn.ccaffa.services.interfaces.RolloProductoService;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rollos_productos")
@Slf4j
public class RolloProductoController {

    private final RolloService rolloService;
    private final OrdenVentaService ordenService;

    public RolloProductoController(RolloService rolloService, OrdenVentaService ordenService) {
        this.rolloService = rolloService;
        this.ordenService = ordenService;
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
    public ResponseEntity<?> obtenerProductoGeneradoPorOrdenDeTrabajoID(@PathVariable Long id) {
        RolloProductoDto rollo = rolloService.findByOrdenDeTrabajoId(id);
        if (rollo == null) {
            ErrorResponse error = ErrorResponse.builder()
                    .status("ROLLO_PRODUCTO_NO_DISPONIBLE")
                    .message("El rollo todavía no esta producido")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        return ResponseEntity.ok(rollo);

    }

    @GetMapping("/obtenerUltimoProductoParaOrdenDeVenta/{id}")
    public ResponseEntity<?> obtenerUltimoProductoParaOrdenDeVentaId(@PathVariable Long id) {

        if (this.ordenService.trabajoFinalizado(id)){
            ErrorResponse error = ErrorResponse.builder()
                    .status("ROLLO_PRODUCTO_NO_DISPONIBLE")
                    .message("El rollo todavía no esta producido")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Optional<RolloProductoDto> rollo = rolloService.findLastByOrdenDeVentaId(id);
        return ResponseEntity.ok(rollo);

    }

}

 */