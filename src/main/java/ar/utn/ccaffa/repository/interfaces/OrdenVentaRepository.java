package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface OrdenVentaRepository extends JpaRepository<OrdenVenta, Long>, JpaSpecificationExecutor<OrdenVenta> {
    OrdenVenta findByOrdenDeTrabajoId(Long ordenDeTrabajoId);
}
