package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import ar.utn.ccaffa.enums.TipoMaterial;
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
public class RolloDto {
    private Long id;
    private Long proveedorId;
    private String codigoProveedor;
    private Float pesoKG;
    private Float anchoMM;
    private Float espesorMM;
    private TipoMaterial tipoMaterial;
    private EstadoRollo estado;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private LocalDateTime fechaIngreso;
    private RolloDto rolloPadre;
}

