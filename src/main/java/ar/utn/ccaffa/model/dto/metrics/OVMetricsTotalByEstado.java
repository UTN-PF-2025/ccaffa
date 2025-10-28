package ar.utn.ccaffa.model.dto.metrics;

import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OVMetricsTotalByEstado {
    private EstadoOrdenVentaEnum estado;
    private Long total;

    public OVMetricsTotalByEstado(EstadoOrdenVentaEnum estado, Long total) {
        this.estado = estado;
        this.total = total;
    }
}
