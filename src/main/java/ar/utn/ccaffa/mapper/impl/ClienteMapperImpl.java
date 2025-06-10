package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.ClienteMapper;
import ar.utn.ccaffa.model.dto.ClienteDto;
import ar.utn.ccaffa.model.entity.Cliente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteDto toDto(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        return ClienteDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getName())
                .email(cliente.getEmail())
                .build();
    }

    @Override
    public Cliente toEntity(ClienteDto clienteDto) {
        if (clienteDto == null) {
            return null;
        }

        return Cliente.builder()
                .id(clienteDto.getId())
                .name(clienteDto.getNombre())
                .email(clienteDto.getEmail())
                .build();
    }

    @Override
    public List<ClienteDto> toDtoList(List<Cliente> clientes) {
        if (clientes == null) {
            return List.of();
        }
        return clientes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Cliente> toEntityList(List<ClienteDto> clienteDtos) {
        if (clienteDtos == null) {
            return List.of();
        }
        return clienteDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}