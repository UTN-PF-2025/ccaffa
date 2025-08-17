package ar.utn.ccaffa.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControlDeCalidadRequestDto {
    private Long ordenTrabajoId;
    private List<MedidaDeCalidadDto> medidas;
}
