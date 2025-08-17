package ar.utn.ccaffa.model.dto;

import java.time.LocalDateTime;

import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroOrdenDeTrabajoDto {
    private Long id;
    private Long rolloId;
    private Long maquinaId;
    private Long ordenDeVentaId;
    private String estado;
    private LocalDateTime fechaIngresoDesde;
    private LocalDateTime fechaIngresoHasta;
    private LocalDateTime fechaFinalizacionDesde;
    private LocalDateTime fechaFinalizacionHasta;
    private MaquinaTipoEnum maquinaTipo;
}
