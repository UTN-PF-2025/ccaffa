package ar.utn.ccaffa.model.dto;

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
    private String estado;
    private String observaciones;
}
