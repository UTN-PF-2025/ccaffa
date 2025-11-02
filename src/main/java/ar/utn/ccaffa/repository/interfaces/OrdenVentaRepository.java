package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import ar.utn.ccaffa.model.dto.metrics.OVMetricsTotalByEstado;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface OrdenVentaRepository extends JpaRepository<OrdenVenta, Long>, JpaSpecificationExecutor<OrdenVenta> {

    @Override
    @EntityGraph(attributePaths = {"especificacion"})
    List<OrdenVenta> findAll();

    @Override
    @EntityGraph(attributePaths = {"especificacion", "cliente"})
    List<OrdenVenta> findAll(Specification<OrdenVenta> spec);
    
    List<OrdenVenta> findByIdIn(List<Long> ids);

    @Query("select ov from OrdenVenta ov left join fetch ov.cliente left join fetch ov.especificacion where ov.id = (select ot.ordenDeVenta.id from OrdenDeTrabajo ot where ot.id = :ordenDeTrabajoId)")
    OrdenVenta findByOrdenDeTrabajoIdFetchClienteEspecificacion(@Param("ordenDeTrabajoId") Long ordenDeTrabajoId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update OrdenVenta ov set ov.estado = :estado where ov.id = :ordenVentaId")
    void updateOrdenDeVentaEstado(@Param("ordenVentaId") Long ordenVentaId, @Param("estado") EstadoOrdenVentaEnum estado);

    @Query("SELECT new ar.utn.ccaffa.model.dto.metrics.OVMetricsTotalByEstado(e.estado, COUNT(e)) FROM OrdenVenta e WHERE e.fechaCreacion >= :fechaCreacionDesde  GROUP BY e.estado")
    List<OVMetricsTotalByEstado> totalByEstado(@Param("fechaCreacionDesde") LocalDateTime fechaCreacionDesde);
}
