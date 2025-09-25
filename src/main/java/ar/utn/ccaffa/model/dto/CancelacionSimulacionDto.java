package ar.utn.ccaffa.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CancelacionSimulacionDto {
    private List<OrdenVentaSimpleDto> ordenesVentaAReplanificar;
    private List<OrdenDeTrabajoResponseDto> ordenesTrabajoACancelar;
    private List<RolloDto> rollosACancelar;
}
