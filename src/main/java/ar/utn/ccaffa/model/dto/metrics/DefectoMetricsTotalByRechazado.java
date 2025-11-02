package ar.utn.ccaffa.model.dto.metrics;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;
import lombok.Data;

@Data
public class DefectoMetricsTotalByRechazado {
    private Boolean esRechazado;
    private Long total;

    public DefectoMetricsTotalByRechazado(Boolean esRechazado, Long total) {
        this.esRechazado = esRechazado;
        this.total = total;
    }
}
