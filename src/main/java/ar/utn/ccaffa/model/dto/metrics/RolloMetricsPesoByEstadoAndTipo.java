package ar.utn.ccaffa.model.dto.metrics;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.TipoMaterial;
import ar.utn.ccaffa.enums.TipoRollo;
import lombok.Data;

@Data
public class RolloMetricsPesoByEstadoAndTipo {
    private EstadoRollo estado;
    private Double peso;
    private TipoMaterial tipoMaterial;
    private TipoRollo tipoRollo;

    public RolloMetricsPesoByEstadoAndTipo(EstadoRollo estado, Double peso, TipoMaterial tipoMaterial, TipoRollo tipoRollo) {
        this.estado = estado;
        this.peso = peso;
        this.tipoMaterial = tipoMaterial;
        this.tipoRollo = tipoRollo;
    }
}
