package ar.utn.ccaffa.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrdenDeTrabajoDto {
    private Long ordenDeVentaId;
    private Long rolloId;
    private List<MaquinaDto> maquinas;
    private String observaciones;

    @Data
    public static class MaquinaDto {
        private Long id;
        private String fechaInicio;
        private String fechaFin;
    }
} 