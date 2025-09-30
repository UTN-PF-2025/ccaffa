package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeTrabajoResponseDto {
    private Long id;
    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoOrdenTrabajoEnum estado;
    private String observaciones;
    private LocalDateTime fechaEstimadaDeInicio;
    private LocalDateTime fechaEstimadaDeFin;
    private Boolean activa;
    private OrdenVentaSimpleDto ordenDeVenta;
    private List<OrdenDeTrabajoMaquinaResponseDto> ordenDeTrabajoMaquinas;
    private RolloDto rollo;
    private ControlDeCalidadDto controlDeCalidad;
} 