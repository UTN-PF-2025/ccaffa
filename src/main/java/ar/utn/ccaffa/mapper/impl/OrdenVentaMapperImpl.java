package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import ar.utn.ccaffa.mapper.interfaces.ClienteMapper;
import ar.utn.ccaffa.mapper.interfaces.EspecificacionMapper;
import ar.utn.ccaffa.mapper.interfaces.OrdenVentaMapper;
import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdenVentaMapperImpl implements OrdenVentaMapper {
    private final ClienteMapper clienteMapper;
    private final EspecificacionMapper especificacionMapper;

    public OrdenVentaMapperImpl(ClienteMapper clienteMapper, EspecificacionMapper especificacionMapper) {
        this.clienteMapper = clienteMapper;
        this.especificacionMapper = especificacionMapper;
    }

    @Override
    public OrdenVentaDto toDto(OrdenVenta ordenVenta) {
        if (ordenVenta == null) {
            return null;
        }

        return OrdenVentaDto.builder()
                .orderId(ordenVenta.getId())
                .fechaCreacion(ordenVenta.getFechaCreacion())
                .fechaEntregaEstimada(ordenVenta.getFechaEntregaEstimada() != null ?
                        ordenVenta.getFechaEntregaEstimada() : null)
                .estado(EstadoOrdenVentaEnum.valueOf(ordenVenta.getEstado().name()))
                .observaciones(ordenVenta.getObservaciones())
                .cliente(clienteMapper.toDto(ordenVenta.getCliente()))
                .especificacion(especificacionMapper.toDto(ordenVenta.getEspecificacion()))
                .build();
    }

    @Override
    public OrdenVenta toEntity(OrdenVentaDto ordenVentaDto) {
        if (ordenVentaDto == null) {
            return null;
        }

        return OrdenVenta.builder()
                .id(ordenVentaDto.getOrderId())
                .fechaCreacion(ordenVentaDto.getFechaCreacion())
                .fechaEntregaEstimada(ordenVentaDto.getFechaEntregaEstimada() != null ?
                        ordenVentaDto.getFechaEntregaEstimada() : null)
                .estado(ordenVentaDto.getEstado())
                .observaciones(ordenVentaDto.getObservaciones())
                .cliente(clienteMapper.toEntity(ordenVentaDto.getCliente()))
                .especificacion(especificacionMapper.toEntity(ordenVentaDto.getEspecificacion()))
                .build();
    }

    @Override
    public List<OrdenVentaDto> toDtoList(List<OrdenVenta> ordenVentas) {
        if (ordenVentas == null) {
            return List.of();
        }
        return ordenVentas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdenVenta> toEntityList(List<OrdenVentaDto> ordenVentaDtos) {
        if (ordenVentaDtos == null) {
            return List.of();
        }
        return ordenVentaDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 