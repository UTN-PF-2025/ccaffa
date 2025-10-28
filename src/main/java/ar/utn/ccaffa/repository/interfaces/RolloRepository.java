package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.model.dto.metrics.OTMMetricsTotalByEstado;
import ar.utn.ccaffa.model.dto.metrics.RolloMetricsPesoByEstadoAndTipo;
import ar.utn.ccaffa.model.dto.metrics.RolloMetricsTotalByEstadoAndTipo;
import ar.utn.ccaffa.model.entity.Rollo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolloRepository extends JpaRepository<Rollo, Long>, JpaSpecificationExecutor<Rollo> {
    List<Rollo> findByRolloPadreId(Long rolloPadreId);
    List<Rollo> findByEstadoInAndAsociadaAOrdenDeTrabajoIs(List<EstadoRollo> estadosRollo, Boolean asociado);
    List<Rollo> findByIdIn(List<Long> ids);
    boolean existsRolloByProveedorIdAndCodigoProveedor(Long proovedorId, String codigoProveedor);

    @Query("SELECT rp FROM Rollo rp LEFT JOIN OrdenDeTrabajo  ot ON ot.id = rp.ordeDeTrabajoAsociadaID  WHERE ot.ordenDeVenta.id = :ordenDeVentaId AND rp.tipoRollo = 'PRODUCTO' ORDER BY rp.id DESC LIMIT 1")
    Optional<Rollo> findLastProductForOrdenDeVentaId(@Param("ordenDeVentaId") Long ordenDeVentaId);

    @Query("SELECT new ar.utn.ccaffa.model.dto.metrics.RolloMetricsTotalByEstadoAndTipo(e.estado, COUNT(e), e.tipoMaterial, e.tipoRollo) FROM Rollo e WHERE e.fechaIngreso >= :fechaIngresoDesde  GROUP BY e.estado, e.tipoMaterial, e.tipoRollo")
    List<RolloMetricsTotalByEstadoAndTipo> total(@Param("fechaIngresoDesde") LocalDateTime fechaIngresoDesde);

    @Query("SELECT new ar.utn.ccaffa.model.dto.metrics.RolloMetricsPesoByEstadoAndTipo(e.estado, SUM(e.pesoKG), e.tipoMaterial, e.tipoRollo) FROM Rollo e WHERE e.fechaIngreso >= :fechaIngresoDesde  GROUP BY e.estado, e.tipoMaterial, e.tipoRollo")
    List<RolloMetricsPesoByEstadoAndTipo> pesoTotal(@Param("fechaIngresoDesde") LocalDateTime fechaIngresoDesde);
}