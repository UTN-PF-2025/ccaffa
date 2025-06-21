package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.repository.interfaces.MaquinaRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.repository.interfaces.RolloRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoDto;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@RequiredArgsConstructor
public class OrdenDeTrabajoController {

    private final OrdenDeTrabajoService ordenDeTrabajoService;
    private final MaquinaRepository maquinaRepository;
    private final RolloRepository rolloRepository;
    private final OrdenVentaRepository ordenDeVentaRepository;

    @PostMapping
    public ResponseEntity<OrdenDeTrabajo> crearOrdenDeTrabajo(@RequestBody OrdenDeTrabajoDto request) {
        OrdenDeTrabajo orden = new OrdenDeTrabajo();
        orden.setObservaciones(request.getObservaciones());

        // Verificar que la Orden de Venta existe (solo para validación)
        if (request.getOrdenDeVentaId() != null) {
            orden.setOrdenesDeVenta(new ArrayList<>());
            var ordenVenta = ordenDeVentaRepository.findById(request.getOrdenDeVentaId());
            if (ordenVenta.isPresent()) {
                orden.getOrdenesDeVenta().add(ordenVenta.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        // Asociar Rollo
        if (request.getRolloId() != null) {
           var rollo =  rolloRepository.findById(request.getRolloId());
           if (rollo.isPresent()) {
            orden.setRollo(rollo.get());
           } else {
            return ResponseEntity.badRequest().build();
           }
        }

        // Asociar Máquinas
        if (request.getMaquinas() != null) {
            List<Maquina> maquinas = new ArrayList<>();
            for (Long mreq : request.getMaquinas()) {
                var maquina = maquinaRepository.findById(mreq);
                if (maquina.isPresent()) {
                    maquinas.add(maquina.get());
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
            orden.setMaquinas(maquinas);
        }

        orden.setFechaEstimadaDeInicio((request.getFechaInicio()));
        orden.setFechaEstimadaDeFin((request.getFechaFin()));
        orden.setObservaciones(request.getObservaciones());
        orden.setEstado("En Proceso");
        
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