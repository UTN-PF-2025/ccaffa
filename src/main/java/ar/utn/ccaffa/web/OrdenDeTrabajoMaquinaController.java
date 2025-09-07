package ar.utn.ccaffa.web;

import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoMaquinaMapper;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoMaquinaDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoMaquinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-trabajo-maquina")
@RequiredArgsConstructor
public class OrdenDeTrabajoMaquinaController {

    private final OrdenDeTrabajoMaquinaService service;
    private final OrdenDeTrabajoMaquinaMapper mapper;

    @GetMapping("/por-maquina/{id}")
    public ResponseEntity<List<OrdenDeTrabajoMaquinaDto>> obtenerOrdenesDeTrabajoPorMaquina(@PathVariable Long id) {
        List<OrdenDeTrabajoMaquina> ordenes = service.findByMaquinaId(id);
        List<OrdenDeTrabajoMaquinaDto> ordenesDto = mapper.toDtoList(ordenes);
        return ResponseEntity.ok(ordenesDto);
    }
}
