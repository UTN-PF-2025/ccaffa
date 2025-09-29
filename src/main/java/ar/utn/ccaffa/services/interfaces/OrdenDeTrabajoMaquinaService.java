package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;

import java.util.List;

public interface OrdenDeTrabajoMaquinaService {
    List<OrdenDeTrabajoMaquina> findByMaquinaId(Long maquinaId);
    OrdenDeTrabajoMaquina findFirstByMaquinaId(Long maquinaId);
    OrdenDeTrabajoMaquina findById(Long id);
}
