package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenVentaDto {
    private Long id;
    private Long orderId;
    private LocalDate fechaCreacion;
    private LocalDate fechaEntregaEstimada;
    private String estado;
    private String observaciones;
    private ClienteDto cliente;
    private List<EspecificacionDto> especificaciones;
}