package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoMaquinaRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoMaquinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenDeTrabajoMaquinaServiceImpl implements OrdenDeTrabajoMaquinaService {

    private final OrdenDeTrabajoMaquinaRepository repository;

    @Override
    public void iniciarOrden(Long idOrden) {
        /*
        implementar inicio de orden de trabajo
         */
    }

    @Override
    public List<OrdenDeTrabajoMaquina> findByMaquinaId(Long maquinaId) {
        return repository.findByMaquinaId(maquinaId);
    }

    @Override
    public Page<OrdenDeTrabajoMaquina> findByMaquinaIdPaginated(Long maquinaId, Pageable pageable) {
        return repository.findByMaquinaIdAndEstadoInOrderByFechaInicioAsc(maquinaId, List.of(EstadoOrdenTrabajoMaquinaEnum.PROGRAMADA, EstadoOrdenTrabajoMaquinaEnum.EN_CURSO), pageable);
    }

    @Override
    public OrdenDeTrabajoMaquina findFirstByMaquinaId(Long maquinaId) {
      return repository.findTopByMaquina_IdAndEstadoOrderByFechaInicioAsc(maquinaId, EstadoOrdenTrabajoMaquinaEnum.PROGRAMADA);
    }

    @Override
    public OrdenDeTrabajoMaquina findById(Long id) {
      return repository.findById(id).orElse(null);
    }
}
