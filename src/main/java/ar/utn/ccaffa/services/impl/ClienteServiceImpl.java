
package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.mapper.interfaces.ClienteMapper;
import ar.utn.ccaffa.model.dto.ClienteDto;
import ar.utn.ccaffa.model.entity.Cliente;
import ar.utn.ccaffa.repository.interfaces.ClienteRepository;
import ar.utn.ccaffa.services.interfaces.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDto> findAll() {
        return clienteMapper.toDtoList(clienteRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDto findById(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
    }

    @Override
    public Cliente save(ClienteDto clienteDto) {
        Cliente cliente = clienteMapper.toEntity(clienteDto);
        return clienteRepository.save(cliente);
    }

    @Override
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }
}