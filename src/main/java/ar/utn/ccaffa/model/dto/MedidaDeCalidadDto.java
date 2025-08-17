package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedidaDeCalidadDto {
    private Long id;
    private String tipo;
    private Float valor;
    private String unidad;
} 