package ar.utn.ccaffa.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import ar.utn.ccaffa.enums.EstadoControlDeCalidadEnum;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.model.entity.MedidaDeCalidad;

@Data
@Builder
public class ControlDeProcesoDto {
    private Long idControl;
    private Long idCliente;
    private String nombreCliente;
    private Long idOrden;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long idMaquina;
    private String nombreMaquina;
    private String tipoMaquina;
    private Long idOperario;
    private String nombreOperario;
    private Long idProveedor;
    private String nombreProveedor;
    private String codigoProveedor;
    private Long idRolloAUsar;
    private String tipoMaterial;
    private Float pesoOriginal;
    private Float anchoOriginal;
    private Float espesorOriginal;
    private Float pesoDeseado;
    private Float anchoDeseado;
    private Float espesorDeseado;
    private Float pesoMaximo;
    private Float toleranciaAncho;
    private Float toleranciaEspesor;
    private Float rebabaMedio;
    private Float anchoMedio;
    private Float espesorMedio;
    private List<MedidaDeCalidad> medidas;
    private List<Defecto> defectos;
    private EstadoControlDeCalidadEnum estado;
}
