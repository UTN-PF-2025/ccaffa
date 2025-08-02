package ar.utn.ccaffa.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class FiltroOrdenVentaDTO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCreacion;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    private String estado;
    private List<String> estados;
    private Long clienteId;
    private String observaciones;
    private Long orderId;
} 