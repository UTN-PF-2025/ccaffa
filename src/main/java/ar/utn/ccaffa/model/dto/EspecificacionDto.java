package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.TipoMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionDto {
    private Long id;
    private Float ancho;
    private Float espesor;
    private Float cantidad;
    private TipoMaterial tipoMaterial;
    private Float pesoMaximoPorRollo;
    private String tipoDeEmbalaje;
    private Float toleranciaAncho;
    private Float toleranciaEspesor;
    private Float diametroInterno;
    private Float diametroExterno;
    private Long ordenVentaId;
} 