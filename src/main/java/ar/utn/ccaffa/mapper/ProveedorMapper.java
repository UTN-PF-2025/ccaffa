package ar.utn.ccaffa.mapper;

import ar.utn.ccaffa.model.dto.ProveedorDto;
import ar.utn.ccaffa.model.entity.Proveedor;

import java.util.List;

public interface ProveedorMapper {
    ProveedorDto toDto(Proveedor entity);
    List<ProveedorDto> toDtoList(List<Proveedor> entities);
    Proveedor toEntity(ProveedorDto dto);
    
}
