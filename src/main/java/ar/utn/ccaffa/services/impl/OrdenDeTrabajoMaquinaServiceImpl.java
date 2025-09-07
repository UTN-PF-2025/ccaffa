package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoMaquinaRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoMaquinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenDeTrabajoMaquinaServiceImpl implements OrdenDeTrabajoMaquinaService {

    private final OrdenDeTrabajoMaquinaRepository repository;

    @Override
    public List<OrdenDeTrabajoMaquina> findByMaquinaId(Long maquinaId) {
        return repository.findByMaquinaId(maquinaId);
    }
}
