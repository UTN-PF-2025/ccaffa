package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    OrdenVenta findByOrdenDeTrabajoId(Long ordenDeTrabajoId);

    @Query("select ov from OrdenVenta ov left join fetch ov.cliente left join fetch ov.especificacion where ov.ordenDeTrabajo.id = :ordenDeTrabajoId")
    OrdenVenta findByOrdenDeTrabajoIdFetchClienteEspecificacion(@Param("ordenDeTrabajoId") Long ordenDeTrabajoId);
}
