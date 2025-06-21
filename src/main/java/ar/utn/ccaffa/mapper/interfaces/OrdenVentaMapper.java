package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.OrdenVenta;

import java.util.List;

public interface OrdenVentaMapper {
    OrdenVentaDto toDto(OrdenVenta ordenVenta);

    OrdenVenta toEntity(OrdenVentaDto ordenVentaDto);

    List<OrdenVentaDto> toDtoList(List<OrdenVenta> ordenVentas);

    List<OrdenVenta> toEntityList(List<OrdenVentaDto> ordenVentaDtos);

}
