/*package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.model.entity.RolloProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolloProductoRepository extends JpaRepository<RolloProducto, Long>, JpaSpecificationExecutor<RolloProducto> {
    List<RolloProducto> findByRolloPadreId(Long rolloId);
    RolloProducto findByOrdenDeTrabajoId(Long rolloId);

    @Query("SELECT rp FROM RolloProudcto rp " +
            "JOIN ordenes_de_trabajo ot" +
                "ON ot.orden_trabajo_id = rp.orden_de_trabajo_id" +
            "JOIN Orden_Venta ov" +
                "ON ov.id = ot.orden_venta_id" +
            "WHERE ov.id = :ordenDeVentaId " +
            "ORDER BY rp.id DESC" +
            "LIMIT 1")
    Optional<RolloProducto> findLastByOrdenDeVentaId(@Param("ordenDeVentaId") Long ordenDeVentaId);

}*/