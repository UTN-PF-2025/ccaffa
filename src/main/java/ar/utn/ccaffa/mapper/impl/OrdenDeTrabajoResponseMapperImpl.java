package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.ClienteMapper;
import ar.utn.ccaffa.mapper.interfaces.EspecificacionMapper;
import ar.utn.ccaffa.mapper.interfaces.MaquinaMapper;
import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoResponseMapper;
import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.*;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdenDeTrabajoResponseMapperImpl implements OrdenDeTrabajoResponseMapper {
    
    private final ClienteMapper clienteMapper;
    private final EspecificacionMapper especificacionMapper;
    private final MaquinaMapper maquinaMapper;
    private final RolloMapper rolloMapper;
    
    public OrdenDeTrabajoResponseMapperImpl(ClienteMapper clienteMapper, 
                                          EspecificacionMapper especificacionMapper,
                                          MaquinaMapper maquinaMapper,
                                          RolloMapper rolloMapper) {
        this.clienteMapper = clienteMapper;
        this.especificacionMapper = especificacionMapper;
        this.maquinaMapper = maquinaMapper;
        this.rolloMapper = rolloMapper;
    }
    
    @Override
    public OrdenDeTrabajoResponseDto toDto(OrdenDeTrabajo ordenDeTrabajo) {
        if (ordenDeTrabajo == null) {
            return null;
        }
        
        return OrdenDeTrabajoResponseDto.builder()
                .id(ordenDeTrabajo.getId())
                .nombre(ordenDeTrabajo.getNombre())
                .fechaInicio(ordenDeTrabajo.getFechaInicio())
                .fechaFin(ordenDeTrabajo.getFechaFin())
                .estado(ordenDeTrabajo.getEstado().name())
                .observaciones(ordenDeTrabajo.getObservaciones())
                .fechaEstimadaDeInicio(ordenDeTrabajo.getFechaEstimadaDeInicio())
                .fechaEstimadaDeFin(ordenDeTrabajo.getFechaEstimadaDeFin())
                .activa(ordenDeTrabajo.getActiva())
                .ordenDeVenta(mapOrdenVentaSimple(ordenDeTrabajo.getOrdenDeVenta()))
                .ordenDeTrabajoMaquinas(mapOrdenDeTrabajoMaquinas(ordenDeTrabajo.getOrdenDeTrabajoMaquinas()))
                .rollo(rolloMapper.toDto(ordenDeTrabajo.getRollo()))
                .controlDeCalidad(mapControlDeCalidad(ordenDeTrabajo.getControlDeCalidad()))
                .build();
    }
    
    @Override
    public List<OrdenDeTrabajoResponseDto> toDtoList(List<OrdenDeTrabajo> ordenesDeTrabajo) {
        if (ordenesDeTrabajo == null) {
            return List.of();
        }
        return ordenesDeTrabajo.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    private OrdenVentaSimpleDto mapOrdenVentaSimple(OrdenVenta ordenVenta) {
        if (ordenVenta == null) {
            return null;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return OrdenVentaSimpleDto.builder()
                .id(ordenVenta.getId())
                .orderId(ordenVenta.getOrderId())
                .fechaCreacion(ordenVenta.getFechaCreacion() != null ? 
                        ordenVenta.getFechaCreacion().format(formatter) : null)
                .fechaEntregaEstimada(ordenVenta.getFechaEntregaEstimada() != null ? 
                        ordenVenta.getFechaEntregaEstimada().format(formatter) : null)
                .estado(ordenVenta.getEstado())
                .observaciones(ordenVenta.getObservaciones())
                .cliente(clienteMapper.toDto(ordenVenta.getCliente()))
                .especificacion(especificacionMapper.toDto(ordenVenta.getEspecificacion()))
                .build();
    }
    
    private List<OrdenDeTrabajoMaquinaResponseDto> mapOrdenDeTrabajoMaquinas(List<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquinas) {
        if (ordenDeTrabajoMaquinas == null) {
            return List.of();
        }
        
        return ordenDeTrabajoMaquinas.stream()
                .map(this::mapOrdenDeTrabajoMaquina)
                .collect(Collectors.toList());
    }
    
    private OrdenDeTrabajoMaquinaResponseDto mapOrdenDeTrabajoMaquina(OrdenDeTrabajoMaquina otm) {
        if (otm == null) {
            return null;
        }
        
        return OrdenDeTrabajoMaquinaResponseDto.builder()
                .id(otm.getId())
                .maquina(maquinaMapper.toDto(otm.getMaquina()))
                .fechaInicio(otm.getFechaInicio())
                .fechaFin(otm.getFechaFin())
                .estado(otm.getEstado().name())
                .observaciones(otm.getObservaciones())
                .build();
    }
    
    private ControlDeCalidadDto mapControlDeCalidad(ControlDeCalidad controlDeCalidad) {
        if (controlDeCalidad == null) {
            return null;
        }
        
        return ControlDeCalidadDto.builder()
                .id(controlDeCalidad.getId())
                .empleado(controlDeCalidad.getUsuario() != null ? 
                    ControlDeCalidadDto.EmpleadoDto.builder()
                        .id(controlDeCalidad.getUsuario().getId())
                        .nombre(controlDeCalidad.getUsuario().getNombre())
                        .build() : null)
                .fechaControl(controlDeCalidad.getFechaControl().toLocalDate())
                .espesorMedido(controlDeCalidad.getEspesorMedido())
                .anchoMedido(controlDeCalidad.getAnchoMedido())
                .rebabaMedio(controlDeCalidad.getRebabaMedio())
                .estado(controlDeCalidad.getEstado())
                .build();
    }
} 