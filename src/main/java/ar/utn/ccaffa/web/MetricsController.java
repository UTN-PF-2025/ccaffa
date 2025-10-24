package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.metrics.OTMMetricsTotalByEstado;
import ar.utn.ccaffa.model.dto.metrics.OTMetricsTotalByEstado;
import ar.utn.ccaffa.model.dto.metrics.OVMetricsTotalByEstado;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoMaquinaRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.repository.interfaces.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    private final UsuarioRepository usuarioRepository;
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenDeTrabajoMaquinaRepository ordenDeTrabajoMaquinaRepository;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;

    public MetricsController(UsuarioRepository usuarioRepository, OrdenVentaRepository ordenVentaRepository, OrdenDeTrabajoMaquinaRepository ordenDeTrabajoMaquinaRepository, OrdenDeTrabajoRepository ordenDeTrabajoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ordenVentaRepository = ordenVentaRepository;
        this.ordenDeTrabajoMaquinaRepository = ordenDeTrabajoMaquinaRepository;
        this.ordenDeTrabajoRepository = ordenDeTrabajoRepository;
    }

    @GetMapping("/usuarios/total")
    public ResponseEntity<Long> totalDeUsuarios() {
        return new ResponseEntity<>(usuarioRepository.count(), HttpStatus.OK);
    }

    @GetMapping("/ordenes_de_venta_by_estado/{fechaCreacionDesde}")
    public ResponseEntity<List<OVMetricsTotalByEstado>>ordenes_de_venta_by_estado(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaCreacionDesde) {
        return new ResponseEntity<>(ordenVentaRepository.totalByEstado(fechaCreacionDesde), HttpStatus.OK);
    }

    @GetMapping("/ordenes_de_trabajo_by_estado/{fechaEstimadaDeInicioDesde}")
    public ResponseEntity<List<OTMetricsTotalByEstado>>ordenes_de_trabajo_by_estado(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaEstimadaDeInicioDesde) {
        return new ResponseEntity<>(ordenDeTrabajoRepository.totalByEstado(fechaEstimadaDeInicioDesde), HttpStatus.OK);
    }

    @GetMapping("/ordenes_de_trabajo_maquina_by_estado/{fechaInicioDesde}")
    public ResponseEntity<List<OTMMetricsTotalByEstado>>ordenes_de_trabajo_maquina_by_estado(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaInicioDesde) {
        return new ResponseEntity<>(ordenDeTrabajoMaquinaRepository.totalByEstado(fechaInicioDesde), HttpStatus.OK);
    }

}
