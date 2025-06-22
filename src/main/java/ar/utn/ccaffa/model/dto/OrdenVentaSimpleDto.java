package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenVentaSimpleDto {
    private Long id;
    private Long orderId;
    private String fechaCreacion;
    private String fechaEntregaEstimada;
    private String estado;
    private String observaciones;
    private ClienteDto cliente;
    private EspecificacionDto especificacion;
} 