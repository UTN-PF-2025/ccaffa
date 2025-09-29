package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenVentaDto {
    private Long orderId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntregaEstimada;
    private String estado;
    private String observaciones;
    private ClienteDto cliente;
    private EspecificacionDto especificacion;
}