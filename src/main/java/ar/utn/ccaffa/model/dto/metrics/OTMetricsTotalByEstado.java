package ar.utn.ccaffa.model.dto.metrics;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;
import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import lombok.Data;

@Data
public class OTMetricsTotalByEstado {
    private EstadoOrdenTrabajoEnum estado;
    private Long total;

    public OTMetricsTotalByEstado(EstadoOrdenTrabajoEnum estado, Long total) {
        this.estado = estado;
        this.total = total;
    }
}
