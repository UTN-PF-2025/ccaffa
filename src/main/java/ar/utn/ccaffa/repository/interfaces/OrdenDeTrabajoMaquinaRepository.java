package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface OrdenDeTrabajoMaquinaRepository extends JpaRepository<OrdenDeTrabajoMaquina, Long> {
    @EntityGraph(attributePaths = {"maquina"})
    OrdenDeTrabajoMaquina findTopByOrdenDeTrabajo_IdOrderByFechaInicioDesc(Long ordenDeTrabajoId);
}