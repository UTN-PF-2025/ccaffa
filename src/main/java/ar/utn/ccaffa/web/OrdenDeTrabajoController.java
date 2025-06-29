package ar.utn.ccaffa.web;

import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoResponseMapper;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoResponseDto;
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
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.model.entity.OrdenVenta;

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
    private final OrdenDeTrabajoResponseMapper ordenDeTrabajoResponseMapper;

    @PostMapping
    public ResponseEntity<OrdenDeTrabajoResponseDto> crearOrdenDeTrabajo(@RequestBody OrdenDeTrabajoDto request) {
        OrdenDeTrabajo orden = new OrdenDeTrabajo();
        orden.setObservaciones(request.getObservaciones());

        // Obtener la Orden de Venta si se proporciona
        OrdenVenta ordenVenta = null;
        if (request.getOrdenDeVentaId() != null) {
            var ordenVentaOpt = ordenDeVentaRepository.findById(request.getOrdenDeVentaId());
            if (ordenVentaOpt.isPresent()) {
                ordenVenta = ordenVentaOpt.get();
                orden.setOrdenDeVenta(ordenVenta);
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
            List<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquinas = new ArrayList<>();
            for (OrdenDeTrabajoDto.MaquinaDto mreq : request.getMaquinas()) {
                var maquina = maquinaRepository.findById(mreq.getId());
                if (maquina.isPresent()) {
                    OrdenDeTrabajoMaquina otm = OrdenDeTrabajoMaquina.builder()
                        .ordenDeTrabajo(orden)
                        .maquina(maquina.get())
                        .fechaInicio(mreq.getFechaInicio())
                        .fechaFin(mreq.getFechaFin())
                        .estado(mreq.getEstado())
                        .observaciones(mreq.getObservaciones())
                        .build();
                    ordenDeTrabajoMaquinas.add(otm);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
            orden.setOrdenDeTrabajoMaquinas(ordenDeTrabajoMaquinas);
        }

        orden.setFechaEstimadaDeInicio((request.getFechaInicio()));
        orden.setFechaEstimadaDeFin((request.getFechaFin()));
        orden.setObservaciones(request.getObservaciones());
        orden.setEstado("En Proceso");

        OrdenDeTrabajo guardada = ordenDeTrabajoService.save(orden);
        if (ordenVenta != null) {
            ordenVenta.setOrdenDeTrabajo(guardada);
            ordenDeVentaRepository.save(ordenVenta);
        }
        
        return ResponseEntity.ok(ordenDeTrabajoResponseMapper.toDto(guardada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDeTrabajoResponseDto> obtenerOrdenDeTrabajo(@PathVariable Long id) {
        return ordenDeTrabajoService.findById(id)
                .map(ordenDeTrabajoResponseMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<OrdenDeTrabajoResponseDto>> obtenerTodasLasOrdenes() {
        List<OrdenDeTrabajo> ordenes = ordenDeTrabajoService.findAll();
        List<OrdenDeTrabajoResponseDto> ordenesDto = ordenDeTrabajoResponseMapper.toDtoList(ordenes);
        return ResponseEntity.ok(ordenesDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenDeTrabajoResponseDto> modificarOrdenDeTrabajo(@PathVariable Long id, @RequestBody OrdenDeTrabajo orden) {
        return ordenDeTrabajoService.update(id, orden)
                .map(ordenDeTrabajoResponseMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<OrdenDeTrabajoResponseDto> cancelarOrdenDeTrabajo(@PathVariable Long id) {
        return ordenDeTrabajoService.cancelar(id)
                .map(ordenDeTrabajoResponseMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/obtenerOrdenesConRollo/{id}")
    public ResponseEntity<List<OrdenDeTrabajoResponseDto>> obtenerOrdenesDeTrabajoConRolloID(@PathVariable Long id) {
        List<OrdenDeTrabajo> ordenes = ordenDeTrabajoService.findByRolloId(id);
        List<OrdenDeTrabajoResponseDto> ordenesDto = ordenDeTrabajoResponseMapper.toDtoList(ordenes);
        return ResponseEntity.ok(ordenesDto);

    }

} 