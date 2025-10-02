package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface OrdenDeTrabajoMaquinaService {
    void iniciarOrden(Long idOrden);

    List<OrdenDeTrabajoMaquina> findByMaquinaId(Long maquinaId);
    Page<OrdenDeTrabajoMaquina> findByMaquinaIdPaginated(Long maquinaId, Pageable pageable);
    OrdenDeTrabajoMaquina findFirstByMaquinaId(Long maquinaId);
    OrdenDeTrabajoMaquina findById(Long id);
}
