package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.TipoMaterial;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroRolloDto {
    private Long proveedorId;
    private String codigoProveedor;
    private Float pesoMin;
    private Float pesoMax;
    private Float anchoMin;
    private Float anchoMax;
    private Float espesorMin;
    private Float espesorMax;
    private TipoMaterial tipoMaterial;
    private EstadoRollo estado;
    private List<EstadoRollo> estados;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private LocalDateTime fechaIngresoDesde;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private LocalDateTime fechaIngresoHasta;
    private Boolean asociadaAOrdenDeTrabajo;
    private Long rolloPadreId;
}

