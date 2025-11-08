package ar.utn.ccaffa.web;

import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.model.dto.*;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoMaquinaRepository;
import ar.utn.ccaffa.services.interfaces.ControlDeCalidadService;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoMaquinaService;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import ar.utn.ccaffa.services.impl.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/controles-calidad")
@RequiredArgsConstructor
@Tag(name = "Controles de Calidad", description = "API para gestionar controles de calidad")
public class ControlDeCalidadController {

    private final ControlDeCalidadService controlDeCalidadService;
    private final OrdenDeTrabajoMaquinaRepository ordenDeTrabajoMaquinaRepository;
    private final RolloService rolloService;
    private final OrdenDeTrabajoMaquinaService ordenDeTrabajoMaquinaService;

    @PostMapping
    public ResponseEntity<?> createControlDeCalidad(@RequestBody CreateControlDeCalidadRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            request.setEmpleadoId(userDetails.getUsuario().getId());
        }
        Optional<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquina = ordenDeTrabajoMaquinaRepository.findById(request.getOrdenDeTrabajoMaquinaId());

        if (ordenDeTrabajoMaquina.isEmpty()){
            ErrorResponse error = ErrorResponse.builder()
                    .status("ORDEN_NO_EXISTENTE")
                    .message("Orden de Trabajo Maquina no encontrada con ID: " + request.getOrdenDeTrabajoMaquinaId())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        if (!rolloService.estaDisponible(ordenDeTrabajoMaquina.get().getRolloAUsar())){
            ErrorResponse error = ErrorResponse.builder()
                    .status("ROLLO_NO_DISPONIBLE")
                    .message("El control de calidad no puede ser iniciado porque su rollo todavía no esta disponible")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        if(this.ordenDeTrabajoMaquinaService.hayOTMEnCursoParaMaquina(ordenDeTrabajoMaquina.get().getMaquina())){
            ErrorResponse error = ErrorResponse.builder()
                    .status("MAQUINA_OCUPADA")
                    .message("El control de calidad no puede ser iniciado porque la maquina se encuentra ocupada")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }


        ControlDeCalidad nuevoControl = controlDeCalidadService.createControlDeCalidad(request);
        return new ResponseEntity<>(nuevoControl, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/medidas")
    public ResponseEntity<ControlDeCalidad> addMedida(
            @PathVariable("id") Long controlDeCalidadId,
            @RequestBody AddMedidaRequest request) {
        ControlDeCalidad updatedControl = controlDeCalidadService.addMedida(controlDeCalidadId, request);
        return ResponseEntity.ok(updatedControl);
    }

    @GetMapping("/{id}/proceso")
    public ResponseEntity<ControlDeProcesoDto> getControlDeProceso(@PathVariable("id") Long controlDeCalidadId) {
        ControlDeProcesoDto procesoDto = controlDeCalidadService.getControlDeProceso(controlDeCalidadId);
        return ResponseEntity.ok(procesoDto);
    }

    @GetMapping()
    @Operation(
        summary = "Obtener controles de calidad con paginación", 
        description = "Retorna una página de controles de calidad filtrados por los criterios especificados"
    )
    public ResponseEntity<Page<ControlDeCalidad>> getAllControlesCalidad(
            FiltroControlDeCalidad filtros,
            @Parameter(description = "Parámetros de paginación") Pageable pageable) {
        Page<ControlDeCalidad> controlesCalidadPage = controlDeCalidadService.filtrarControlesCalidad(filtros, pageable);
        return ResponseEntity.ok(controlesCalidadPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ControlDeCalidad> getControlDeCalidadById(@PathVariable Long id) {
        ControlDeCalidad controlDeCalidad = controlDeCalidadService.getControlDeCalidadById(id);
        return ResponseEntity.ok(controlDeCalidad);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<List<RolloDto>> finalizarControl(@PathVariable Long id) {
        List<RolloDto> rollosAEtiquetar = controlDeCalidadService.finalizarControl(id);
        return ResponseEntity.ok(rollosAEtiquetar);
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<ControlDeCalidad> iniciarControl(@PathVariable Long id) {
        ControlDeCalidad controlDeCalidad = controlDeCalidadService.iniciarControl(id);
        return ResponseEntity.ok(controlDeCalidad);
    }

    @PutMapping("/{id}/a-corregir")
    public ResponseEntity<ControlDeCalidad> marcarComoACorregir(@PathVariable Long id) {
        ControlDeCalidad controlDeCalidad = controlDeCalidadService.marcarComoACorregir(id);
        return ResponseEntity.ok(controlDeCalidad);
    }
}
