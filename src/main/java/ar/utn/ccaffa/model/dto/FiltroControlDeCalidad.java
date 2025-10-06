package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoControlDeCalidadEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroControlDeCalidad {
    private Long usuarioId;
    private Long ordenDeTrabajoMaquinaId;
    private List<EstadoControlDeCalidadEnum> estados;
}
