package ar.utn.ccaffa.model.dto;

import lombok.Data;

@Data
public class CreateControlDeCalidadRequest {
    private Long empleadoId;
    private Long ordenDeTrabajoId;
}
