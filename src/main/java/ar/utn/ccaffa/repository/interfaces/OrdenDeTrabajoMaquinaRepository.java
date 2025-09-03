package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenDeTrabajoMaquinaRepository extends JpaRepository<OrdenDeTrabajoMaquina, Long> {
    List<OrdenDeTrabajoMaquina> findOrdenDeTrabajoMaquinaByEstadoAndFechaFinAfterAndFechaFinBeforeAndMaquinaIn(String estado, LocalDateTime fechaFinDesde, LocalDateTime fecaFinHasta, List<Maquina> maquinas);
} 