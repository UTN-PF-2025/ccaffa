package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.ProveedorMapper;
import ar.utn.ccaffa.model.dto.ProveedorDto;
import ar.utn.ccaffa.model.entity.Proveedor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorMapperImpl implements ProveedorMapper {
    @Override
    public ProveedorDto toDto(Proveedor entity) {
        return ProveedorDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .activo(entity.getActivo())
                .build();
    }

    @Override
    public List<ProveedorDto> toDtoList(List<Proveedor> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    @Override
    public Proveedor toEntity(ProveedorDto dto) {
        return Proveedor.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .activo(dto.getActivo())
                .build();
    }
}
