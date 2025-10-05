package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.model.entity.Rollo;

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

    @Query("SELECT rp FROM Rollo rp JOIN OrdenDeTrabajo  ot ON ot.id = rp.ordeDeTrabajoAsociadaID JOIN OrdenVenta ov ON ov.id = ot.ordenDeVenta.id WHERE ov.id = :ordenDeVentaId AND rp.tipoRollo = 'PRODUCTO' ORDER BY rp.id DESC LIMIT 1")
    Optional<Rollo> findLastProductForOrdenDeVentaId(@Param("ordenDeVentaId") Long ordenDeVentaId);

}