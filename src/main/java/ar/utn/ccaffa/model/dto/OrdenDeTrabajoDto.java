package ar.utn.ccaffa.model.dto;

import lombok.Data;

import java.sql.Time;
import java.util.List;



@Data
public class OrdenDeTrabajoDto {
    private Long ordenDeVentaId;
    private Long rolloId;
    private List<Long> maquinas;
    private Time fechaInicio;
    private Time fechaFin;
    private String observaciones;
} 