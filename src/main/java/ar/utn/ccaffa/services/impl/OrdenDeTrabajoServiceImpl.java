package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdenDeTrabajoServiceImpl implements OrdenDeTrabajoService {
    private final OrdenDeTrabajoRepository repository;

    @Override
    public OrdenDeTrabajo save(OrdenDeTrabajo orden) {
        return repository.save(orden);
    }

    @Override
    public List<OrdenDeTrabajo> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<OrdenDeTrabajo> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<OrdenDeTrabajo> update(Long id, OrdenDeTrabajo orden) {
        return repository.findById(id).map(existing -> {
            orden.setId(id);
            return repository.save(orden);
        });
    }

    @Override
    public Optional<OrdenDeTrabajo> cancelar(Long id) {
        return repository.findById(id).map(existing -> {
            existing.setActiva(false);
            return repository.save(existing);
        });
    }

    @Override
    public List<OrdenDeTrabajo> findByRolloId(Long rolloId){
        return repository.findByRolloId(rolloId);
    };
} 