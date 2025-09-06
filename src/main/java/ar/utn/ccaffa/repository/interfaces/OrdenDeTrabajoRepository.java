package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdenDeTrabajoRepository extends JpaRepository<OrdenDeTrabajo, Long>, JpaSpecificationExecutor<OrdenDeTrabajo> {
    List<OrdenDeTrabajo> findByRolloId(Long rolloId);
    List<OrdenDeTrabajo> findByOrdenDeVenta_Id(Long ordenDeVentaId);

    @Query("select ot from OrdenDeTrabajo ot left join fetch ot.rollo where ot.id = :id")
    Optional<OrdenDeTrabajo> findByIdFetchRollo(@Param("id") Long id);

}