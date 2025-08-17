
package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.ClienteDto;
import ar.utn.ccaffa.model.entity.Cliente;

import java.util.List;

public interface ClienteService {
    List<ClienteDto> findAll();
    ClienteDto findById(Long id);
    Cliente save(ClienteDto clienteDto);
    void deleteById(Long id);
}