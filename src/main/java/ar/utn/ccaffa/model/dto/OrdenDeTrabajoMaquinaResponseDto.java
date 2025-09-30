package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeTrabajoMaquinaResponseDto {
    private Long id;
    private MaquinaDto maquina;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoOrdenTrabajoMaquinaEnum estado;
    private String observaciones;
} 