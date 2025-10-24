package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.metrics.*;
import ar.utn.ccaffa.repository.interfaces.*;
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
    private final RolloRepository rolloRepository;

    public MetricsController(UsuarioRepository usuarioRepository, OrdenVentaRepository ordenVentaRepository, OrdenDeTrabajoMaquinaRepository ordenDeTrabajoMaquinaRepository, OrdenDeTrabajoRepository ordenDeTrabajoRepository, RolloRepository rolloRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ordenVentaRepository = ordenVentaRepository;
        this.ordenDeTrabajoMaquinaRepository = ordenDeTrabajoMaquinaRepository;
        this.ordenDeTrabajoRepository = ordenDeTrabajoRepository;
        this.rolloRepository = rolloRepository;
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

    @GetMapping("/rollos_total/{fechaIngresoDesde}")
    public ResponseEntity<List<RolloMetricsTotalByEstadoAndTipo>>rollos_total(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaIngresoDesde) {
        return new ResponseEntity<>(rolloRepository.total(fechaIngresoDesde), HttpStatus.OK);
    }

    @GetMapping("/rollos_pesoTotal/{fechaIngresoDesde}")
    public ResponseEntity<List<RolloMetricsPesoByEstadoAndTipo>>rollos_pesoTotal(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaIngresoDesde) {
        return new ResponseEntity<>(rolloRepository.pesoTotal(fechaIngresoDesde), HttpStatus.OK);
    }

}
