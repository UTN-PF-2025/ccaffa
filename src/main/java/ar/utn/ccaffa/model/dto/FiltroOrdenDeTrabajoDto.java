package ar.utn.ccaffa.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;
import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroOrdenDeTrabajoDto {
    private Long id;
    private Long rolloId;
    private Long rolloProductoId;
    private Long ordenDeVentaId;
    private List<EstadoOrdenTrabajoEnum> estados;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicioDesde;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicioHasta;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFinalizacionDesde;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFinalizacionHasta;
}
