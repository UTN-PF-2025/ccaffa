package ar.utn.ccaffa.web;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.TipoMaterial;
import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.model.dto.CancelacionSimulacionDto;
import ar.utn.ccaffa.model.dto.FiltroRolloDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.dto.SimulacionAnulacionRolloResponse;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rollos")
@Slf4j
@Tag(name = "Rollos", description = "API para gestionar rollos")
public class RolloController {

    private final RolloService rolloService;
    private final OrdenVentaService ordenService;

    public RolloController(RolloService rolloService, OrdenVentaService ordenService) {
        this.rolloService = rolloService;
        this.ordenService = ordenService;
    }

    @GetMapping
    @Operation(
        summary = "Obtener rollos con paginación", 
        description = "Retorna una página de rollos filtrados por los criterios especificados"
    )
    public ResponseEntity<Page<RolloDto>> getAllRollos(
            FiltroRolloDto filtros,
            @Parameter(description = "Parámetros de paginación") Pageable pageable) {
        log.info("Obteniendo rollos paginados");
        Page<RolloDto> rollosPage = rolloService.filtrarRollos(filtros, pageable);
        return ResponseEntity.ok(rollosPage);
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
    public ResponseEntity<?> createRollo(@RequestBody RolloDto rollo) {
        log.info("Creando nuevo rollo: {}", rollo);
        rollo.setId(null);
        rollo.setEstado(EstadoRollo.DISPONIBLE);
        rollo.setOrdeDeTrabajoAsociadaID(null);
        rollo.setAsociadaAOrdenDeTrabajo(false);
        rollo.setFechaIngreso(LocalDateTime.now());
        if (rolloService.existsRolloByProveedorIdAndCodigoProveedor(rollo.getProveedorId(), rollo.getCodigoProveedor())){
            ErrorResponse error = ErrorResponse.builder()
                    .status("REPEATED_PROVIDER_CODE_AND_PROVIDER")
                    .message("Ya existe un rollo con el mismo codigo y proveedor")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        RolloDto savedRollo = rolloService.save(rollo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRollo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRollo(@PathVariable Long id, @RequestBody RolloDto rollo) {
        rollo.setId(id);
        try {
            return ResponseEntity.ok(rolloService.modify(rollo));
        }
        catch (Exception e){
            ErrorResponse error = ErrorResponse.builder()
                    .status("ERROR_EN_LA_MODIFICACION")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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
    public ResponseEntity<?> anularRollo(@PathVariable Long id) {
        log.info("Anulando rollo con ID: {}", id);
        try {
            rolloService.anularRollo(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status("ERROR_EN_LA_ANULACION")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

    }

    @GetMapping("/{id}/simular-anulacion")
    public ResponseEntity<?> simularCancelacion(@PathVariable Long id) {
        try {
            CancelacionSimulacionDto simulacion = rolloService.simularCancelacion(id);
            return ResponseEntity.ok(simulacion);
        } catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status("ERROR_EN_LA_SIMULACION")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}/anular")
    public ResponseEntity<SimulacionAnulacionRolloResponse> simularAnularRollo(@PathVariable Long id) {
        log.info("Se simula Anulacion de rollo con ID: {}", id);
        SimulacionAnulacionRolloResponse response = new SimulacionAnulacionRolloResponse(rolloService.simularAnularRollo(id));
        return ResponseEntity.ok(response);
    }

}