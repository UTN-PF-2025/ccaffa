package ar.utn.ccaffa.model.dto.metrics;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;
import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import lombok.Data;

@Data
public class OTMMetricsTotalByEstado {
    private EstadoOrdenTrabajoMaquinaEnum estado;
    private Long total;

    public OTMMetricsTotalByEstado(EstadoOrdenTrabajoMaquinaEnum estado, Long total) {
        this.estado = estado;
        this.total = total;
    }
}
