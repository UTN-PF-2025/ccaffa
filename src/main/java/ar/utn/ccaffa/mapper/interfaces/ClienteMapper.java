package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.model.dto.ClienteDto;
import ar.utn.ccaffa.model.entity.Cliente;

import java.util.List;

public interface ClienteMapper {
    ClienteDto toDto(Cliente cliente);

    Cliente toEntity(ClienteDto clienteDto);

    List<ClienteDto> toDtoList(List<Cliente> clientes);

    List<Cliente> toEntityList(List<ClienteDto> clienteDtos);

}
