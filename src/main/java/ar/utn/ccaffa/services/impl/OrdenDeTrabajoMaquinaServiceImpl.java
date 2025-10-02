package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;
import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoMaquinaRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoMaquinaService;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenDeTrabajoMaquinaServiceImpl implements OrdenDeTrabajoMaquinaService {

    private final OrdenDeTrabajoMaquinaRepository repository;
    private final OrdenDeTrabajoService ordenDeTrabajoService;

    @Override
    public void iniciarOrden(Long idOrden) {
        OrdenDeTrabajo ot = this.ordenDeTrabajoService.findById(idOrden).orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", "id", idOrden));

        if(estaDisponible(ot.getRollo())){
            OrdenDeTrabajoMaquina ordenTrabajoMaquinaInicial = ot.getOrdenDeTrabajoMaquinas().getFirst();
            OrdenDeTrabajoMaquina result = iniciarOrdenTrabajoMaquina(ordenTrabajoMaquinaInicial);

        } else {
            throw new IllegalStateException("La orden de trabajo maquina no puede ser iniciada porque su rollo no esta en estado disponible");
        }
    }

    private boolean estaDisponible(Rollo rollo) {

        return false;
    }

    private OrdenDeTrabajoMaquina iniciarOrdenTrabajoMaquina(OrdenDeTrabajoMaquina ordenTrabajoMaquinaInicial) {
        ordenTrabajoMaquinaInicial.setEstado(EstadoOrdenTrabajoMaquinaEnum.EN_CURSO);
        OrdenDeTrabajoMaquina savedOrden = repository.save(ordenTrabajoMaquinaInicial);
        return savedOrden;
    }

    @Override
    public List<OrdenDeTrabajoMaquina> findByMaquinaId(Long maquinaId) {
        return repository.findByMaquinaId(maquinaId);
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
