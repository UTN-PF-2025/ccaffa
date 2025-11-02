package ar.utn.ccaffa.model.dto.metrics;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.TipoMaterial;
import ar.utn.ccaffa.enums.TipoRollo;
import lombok.Data;

@Data
public class RolloMetricsTotalByEstadoAndTipo {
    private EstadoRollo estado;
    private Long total;
    private TipoMaterial tipoMaterial;
    private TipoRollo tipoRollo;

    public RolloMetricsTotalByEstadoAndTipo(EstadoRollo estado, Long total, TipoMaterial tipoMaterial, TipoRollo tipoRollo) {
        this.estado = estado;
        this.total = total;
        this.tipoMaterial = tipoMaterial;
        this.tipoRollo = tipoRollo;
    }
}
