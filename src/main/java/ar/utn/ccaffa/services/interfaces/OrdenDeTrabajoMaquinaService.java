package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import java.util.List;
public interface OrdenDeTrabajoMaquinaService {
    void iniciarOrden(Long idOrden);

    List<OrdenDeTrabajoMaquina> findByMaquinaId(Long maquinaId);
    OrdenDeTrabajoMaquina findFirstByMaquinaId(Long maquinaId);
}
