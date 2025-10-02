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
    private Long idOperario;
    private String nombreOperario;
    private Double cantidad;
    private String tipoMaterial;
    private Float ancho;
    private Float toleranciaAncho;
    private Float espesor;
    private Float toleranciaEspesor;
    private String dureza;
    private Float tamanoRebaba;
    private Long idProveedor;
    private String nombreProveedor;
    private String codigoEtiquetaMp;
    private List<MedidaDeCalidad> medidas;
    private List<Defecto> defectos;
    private EstadoControlDeCalidadEnum estado;
}
