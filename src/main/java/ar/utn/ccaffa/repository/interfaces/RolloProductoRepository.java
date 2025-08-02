package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.model.entity.RolloProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RolloProductoRepository extends JpaRepository<RolloProducto, Long>, JpaSpecificationExecutor<RolloProducto> {
    List<RolloProducto> findByRolloPadreId(Long rolloId);
    List<RolloProducto> findByOrdenDeTrabajoId(Long rolloId);

}