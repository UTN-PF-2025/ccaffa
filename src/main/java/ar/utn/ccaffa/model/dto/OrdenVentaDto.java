package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private EstadoOrdenVentaEnum estado;
    private String observaciones;
    private ClienteDto cliente;
    private EspecificacionDto especificacion;
}