package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenVentaDto {
    private Long id;
    private Long orderId;
    private LocalDate fechaCreacion;
    private LocalDate fechaEntregaEstimada;
    private EstadoOrdenVentaEnum estado;
    private String observaciones;
    private ClienteDto cliente;
    private EspecificacionDto especificacion;
}