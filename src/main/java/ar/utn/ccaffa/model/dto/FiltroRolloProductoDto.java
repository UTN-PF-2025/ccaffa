package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.EstadoRolloProducto;
import ar.utn.ccaffa.enums.TipoMaterial;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroRolloProductoDto {
    private Float pesoMin;
    private Float pesoMax;
    private Float anchoMin;
    private Float anchoMax;
    private Float espesorMin;
    private Float espesorMax;
    private TipoMaterial tipoMaterial;
    private EstadoRolloProducto estado;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private LocalDateTime fechaIngresoDesde;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private LocalDateTime fechaIngresoHasta;
    private Long rolloPadreId;
    private Long ordenDeTrabajoId;
}

