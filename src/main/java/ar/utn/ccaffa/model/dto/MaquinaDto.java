package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaquinaDto {
    private Integer id;
    private String nombre;
    private MaquinaTipoEnum tipo;
    private Float velocidadTrabajo;
    private String estado;
}
