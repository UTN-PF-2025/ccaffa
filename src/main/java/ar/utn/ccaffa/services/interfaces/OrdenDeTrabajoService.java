package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import java.util.List;
import java.util.Optional;

public interface OrdenDeTrabajoService {
    OrdenDeTrabajo save(OrdenDeTrabajo orden);
    List<OrdenDeTrabajo> findAll();
    Optional<OrdenDeTrabajo> findById(Long id);
    Optional<OrdenDeTrabajo> update(Long id, OrdenDeTrabajo orden);
    Optional<OrdenDeTrabajo> cancelar(Long id);
    List<OrdenDeTrabajo> findByRolloId(Long rolloId);
} 