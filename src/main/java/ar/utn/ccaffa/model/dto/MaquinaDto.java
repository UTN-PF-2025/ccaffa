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
    private Long id;
    private String nombre;
    private Boolean activo;
    private MaquinaTipoEnum tipo;
    private Float velocidadTrabajoMetrosPorMinuto;
    private Float espesorMaximoMilimetros;
    private Float espesorMinimoMilimetros;
    private Float anchoMaximoMilimetros;
    private Float anchoMinimoMilimetros;
}
