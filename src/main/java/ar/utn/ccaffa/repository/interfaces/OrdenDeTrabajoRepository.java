package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;
import ar.utn.ccaffa.model.dto.metrics.OTMetricsTotalByEstado;
import ar.utn.ccaffa.model.dto.metrics.OVMetricsTotalByEstado;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenDeTrabajoRepository extends JpaRepository<OrdenDeTrabajo, Long>, JpaSpecificationExecutor<OrdenDeTrabajo> {
    List<OrdenDeTrabajo> findByRolloId(Long rolloId);
    @EntityGraph(attributePaths = {"ordenDeTrabajoMaquinas.maquina", "rollo", "ordenDeVenta.especificacion", "ordenDeVenta.cliente"})
    OrdenDeTrabajo findByOrdenDeTrabajoMaquinas_Id(Long ordenDeTrabajoMaquinaId);
    Optional<OrdenDeTrabajo> findTopByOrdenDeVenta_IdAndEstadoInOrderByIdDesc(Long ordenDeVentaId, List<EstadoOrdenTrabajoEnum> estados);



    @Override
    @EntityGraph(attributePaths = {"ordenDeTrabajoMaquinas.maquina", "rollo", "rolloProducto", "ordenDeVenta.especificacion", "ordenDeVenta.cliente"})
    List<OrdenDeTrabajo> findAll(Specification<OrdenDeTrabajo> spec);

    @Query("select ot from OrdenDeTrabajo ot left join fetch ot.rollo where ot.id = :id")
    Optional<OrdenDeTrabajo> findByIdFetchRollo(@Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = {"ordenDeTrabajoMaquinas.maquina", "rollo", "rolloProducto", "ordenDeVenta.especificacion", "ordenDeVenta.cliente"})
    Optional<OrdenDeTrabajo> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"ordenDeTrabajoMaquinas.maquina", "rollo", "rolloProducto", "ordenDeVenta.especificacion", "ordenDeVenta.cliente"})
    List<OrdenDeTrabajo> findAll();

    @Query("SELECT new ar.utn.ccaffa.model.dto.metrics.OTMetricsTotalByEstado(e.estado, COUNT(e)) FROM OrdenDeTrabajo e WHERE e.fechaEstimadaDeInicio >= :fechaEstimadaDeInicioDesde  GROUP BY e.estado")
    List<OTMetricsTotalByEstado> totalByEstado(@Param("fechaEstimadaDeInicioDesde") LocalDateTime fechaEstimadaDeInicioDesde);

}