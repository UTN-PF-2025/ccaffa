package ar.utn.ccaffa.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DefectoDto {
    private Long id;
    private String imagen;
    private LocalDate fecha;
    private String tipo;
    private String descripcion;
    private Boolean esRechazado;
    private Long controlDeCalidadId;
}
