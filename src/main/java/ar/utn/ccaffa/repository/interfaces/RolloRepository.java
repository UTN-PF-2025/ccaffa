package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.model.entity.Rollo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RolloRepository extends JpaRepository<Rollo, Long>, JpaSpecificationExecutor<Rollo> {
    List<Rollo> findByRolloPadreId(Long rolloPadreId);
    List<Rollo> findByEstadoIn(List<EstadoRollo> estadosRollo);
    List<Rollo> findByIdIn(List<Long> ids);
    boolean existsRolloByProveedorIdAndCodigoProveedor(Long proovedorId, String codigoProveedor);
}