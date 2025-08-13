package ar.utn.ccaffa.model.dto;

import lombok.Data;

@Data
public class AnalysisResponse {
    private boolean defect;
    private String details;
    private String imageId; // Opcional, para asociar la respuesta a una imagen guardada
}
