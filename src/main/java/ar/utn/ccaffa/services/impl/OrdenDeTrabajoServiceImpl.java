package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.dto.FiltroOrdenDeTrabajoDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
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
    public List<OrdenDeTrabajo> findByRolloId(Long rolloId) {
        return repository.findByRolloId(rolloId);
    }

    @Override
    public List<OrdenDeTrabajo> filtrarOrdenes(FiltroOrdenDeTrabajoDto filtros) {
        Specification<OrdenDeTrabajo> spec = Specification.where(null);

        if (filtros.getRolloId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("rolloId"), filtros.getRolloId()));
        }

        if (filtros.getMaquinaId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("maquinaId"), filtros.getMaquinaId()));
        }

        if (filtros.getOrdenDeVentaId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ordenDeVentaId"), filtros.getOrdenDeVentaId()));
        }

        if (filtros.getEstado() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), filtros.getEstado()));
        }

        if (filtros.getFechaIngresoDesde() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaIngreso"), filtros.getFechaIngresoDesde()));
        }

        if (filtros.getFechaIngresoHasta() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaIngreso"), filtros.getFechaIngresoHasta()));
        }

        if (filtros.getFechaFinalizacionDesde() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaFinalizacion"), filtros.getFechaFinalizacionDesde()));
        }

        if (filtros.getFechaFinalizacionHasta() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaFinalizacion"), filtros.getFechaFinalizacionHasta()));
        }

        if (filtros.getMaquinaTipo() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("maquinaTipo"), filtros.getMaquinaTipo()));
        }

        return repository.findAll(spec);
    }
}