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
    private String name;
    private MaquinaTipoEnum type;
    private Float velocidadTrabajo;
    private String estado;
}
