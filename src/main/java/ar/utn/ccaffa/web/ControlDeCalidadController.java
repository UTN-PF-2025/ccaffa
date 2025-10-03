package ar.utn.ccaffa.web;

import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.model.dto.AddMedidaRequest;
import ar.utn.ccaffa.model.dto.ControlDeProcesoDto;
import ar.utn.ccaffa.model.dto.CreateControlDeCalidadRequest;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.Usuario;
import ar.utn.ccaffa.services.interfaces.ControlDeCalidadService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import ar.utn.ccaffa.services.impl.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/controles-calidad")
@RequiredArgsConstructor
public class ControlDeCalidadController {

    private final ControlDeCalidadService controlDeCalidadService;

    @PostMapping
    public ResponseEntity<ControlDeCalidad> createControlDeCalidad(@RequestBody CreateControlDeCalidadRequest request) {
        if (request.getEmpleadoId() == null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl userDetails) {
                request.setEmpleadoId(userDetails.getUsuario().getId());
            }
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
    public ResponseEntity<List<ControlDeCalidad>> getAllControlesCalidad() {
        List<ControlDeCalidad> controlesCalidad = controlDeCalidadService.getAllControlesCalidad();
        return ResponseEntity.ok(controlesCalidad);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ControlDeCalidad> finalizarControl(@PathVariable Long id) {
        ControlDeCalidad controlDeCalidad = controlDeCalidadService.finalizarControl(id);
        return ResponseEntity.ok(controlDeCalidad);
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<?> iniciarControl(@PathVariable Long id) {
        try {
            ControlDeCalidad controlDeCalidad = controlDeCalidadService.iniciarControl(id);
            return ResponseEntity.ok(controlDeCalidad);
        } catch (IllegalStateException e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status("UNABLE_TO_START_JOB")
                    .message("No se puede iniciar control de calidad debido a que el rollo o la maquina no esta disponible")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}/a-corregir")
    public ResponseEntity<ControlDeCalidad> marcarComoACorregir(@PathVariable Long id) {
        ControlDeCalidad controlDeCalidad = controlDeCalidadService.marcarComoACorregir(id);
        return ResponseEntity.ok(controlDeCalidad);
    }
}
