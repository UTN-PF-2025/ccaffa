package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrdenVentaRepository extends JpaRepository<OrdenVenta, Long>, JpaSpecificationExecutor<OrdenVenta> {
    List<OrdenVenta> findByIdIn(List<Long> ids);

}
