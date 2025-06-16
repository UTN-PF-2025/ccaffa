package ar.utn.ccaffa.service.impl;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.service.OrdenDeTrabajoService;
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
} 