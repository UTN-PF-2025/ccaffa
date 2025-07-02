package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenDeTrabajoRepository extends JpaRepository<OrdenDeTrabajo, Long> {
    List<OrdenDeTrabajo> findByRolloId(Long rolloId);
    List<OrdenDeTrabajo> findByOrdenDeVenta_Id(Long ordenDeVentaId);

}