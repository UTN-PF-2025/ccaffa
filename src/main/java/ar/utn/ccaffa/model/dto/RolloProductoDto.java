package ar.utn.ccaffa.model.dto;

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
public class RolloProductoDto {
    private Long id;
    private Float pesoKG;
    private Float anchoMM;
    private Float espesorMM;
    private TipoMaterial tipoMaterial;
    private EstadoRolloProducto estado;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private LocalDateTime fechaIngreso;
    private Long rolloPadreId;
    private Long ordenDeTrabajoId;
}

