package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.repository.interfaces.MaquinaRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.repository.OrdenDeVentaRepository;
import ar.utn.ccaffa.repository.RolloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@RequiredArgsConstructor
public class OrdenDeTrabajoController {

    private final OrdenDeTrabajoService ordenDeTrabajoService;
    private final MaquinaRepository maquinaRepository;
    private final RolloRepository rolloRepository;
    private final OrdenDeVentaRepository ordenDeVentaRepository;

    @PostMapping
    public ResponseEntity<OrdenDeTrabajo> crearOrdenDeTrabajo(@RequestBody OrdenDeTrabajoDto request) {
        OrdenDeTrabajo orden = new OrdenDeTrabajo();
        orden.setObservaciones(request.getObservaciones());

        // Asociar Orden de Venta
        if (request.getOrdenDeVentaId() != null) {
            orden.setOrdenesDeVenta(new ArrayList<>());
            ordenDeVentaRepository.findById(request.getOrdenDeVentaId()).ifPresent(ov -> orden.getOrdenesDeVenta().add(ov));
        }

        // Asociar Rollo
        if (request.getRolloId() != null) {
            rolloRepository.findById(request.getRolloId()).ifPresent(orden::setRollo);
        }

        // Asociar Máquinas
        if (request.getMaquinas() != null) {
            List<Maquina> maquinas = new ArrayList<>();
            for (OrdenDeTrabajoDto.MaquinaDto mreq : request.getMaquinas()) {
                maquinaRepository.findById(mreq.getId()).ifPresent(maquinas::add);
            }
            orden.setMaquinas(maquinas);
        }

        OrdenDeTrabajo guardada = ordenDeTrabajoService.save(orden);
        return ResponseEntity.ok(guardada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDeTrabajo> obtenerOrdenDeTrabajo(@PathVariable Long id) {
        return ordenDeTrabajoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenDeTrabajo> modificarOrdenDeTrabajo(@PathVariable Long id, @RequestBody OrdenDeTrabajo orden) {
        return ordenDeTrabajoService.update(id, orden)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<OrdenDeTrabajo> cancelarOrdenDeTrabajo(@PathVariable Long id) {
        return ordenDeTrabajoService.cancelar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 