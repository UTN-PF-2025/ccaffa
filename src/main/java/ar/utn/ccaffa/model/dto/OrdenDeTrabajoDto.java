package ar.utn.ccaffa.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdenDeTrabajoDto {
    private Long ordenDeVentaId;
    private Long rolloId;
    private List<MaquinaDto> maquinas;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String observaciones;
    
    @Data
    public static class MaquinaDto {
        private Long id;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private String estado;
        private String observaciones;
    }
} 