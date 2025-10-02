package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenVentaDto {
    private Long orderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private LocalDateTime fechaCreacion;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private LocalDateTime fechaEntregaEstimada;
    private EstadoOrdenVentaEnum estado;
    private String observaciones;
    private ClienteDto cliente;
    private EspecificacionDto especificacion;
}