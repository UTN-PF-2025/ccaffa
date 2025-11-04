package ar.utn.ccaffa.model.dto.metrics;

import ar.utn.ccaffa.enums.EstadoControlDeCalidadEnum;
import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import lombok.Data;

@Data
public class ControlDeCalidadMetricsTotalByEstado {
    private EstadoControlDeCalidadEnum estado;
    private Long total;

    public ControlDeCalidadMetricsTotalByEstado(EstadoControlDeCalidadEnum estado, Long total) {
        this.estado = estado;
        this.total = total;
    }
}
