package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrdenDeTrabajoRepository extends JpaRepository<OrdenDeTrabajo, Long>, JpaSpecificationExecutor<OrdenDeTrabajo> {
    List<OrdenDeTrabajo> findByRolloId(Long rolloId);
}