package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.AddMedidaRequest;
import ar.utn.ccaffa.model.dto.ControlDeProcesoDto;
import ar.utn.ccaffa.model.dto.CreateControlDeCalidadRequest;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.services.interfaces.ControlDeCalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/controles-calidad")
@RequiredArgsConstructor
public class ControlDeCalidadController {

    private final ControlDeCalidadService controlDeCalidadService;

    @PostMapping
    public ResponseEntity<ControlDeCalidad> createControlDeCalidad(@RequestBody CreateControlDeCalidadRequest request) {
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
}
