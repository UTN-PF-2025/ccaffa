package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;
import ar.utn.ccaffa.model.entity.Rollo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrdenDeTrabajoMaquinaDto {
    private Long id;
    private Long ordenDeTrabajoId;
    private Long maquinaId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoOrdenTrabajoMaquinaEnum estado;
    private String observaciones;
    private RolloDto rolloAUsar;
}
