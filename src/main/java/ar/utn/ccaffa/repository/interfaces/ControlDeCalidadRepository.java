package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.dto.metrics.ControlDeCalidadMetricsTotalByEstado;
import ar.utn.ccaffa.model.dto.metrics.OVMetricsTotalByEstado;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ControlDeCalidadRepository extends JpaRepository<ControlDeCalidad, Long>, JpaSpecificationExecutor<ControlDeCalidad> {
    @Query("SELECT c FROM ControlDeCalidad c LEFT JOIN FETCH c.medidasDeCalidad WHERE c.id = :id")
    Optional<ControlDeCalidad> findByIdWithMedidas(@Param("id") Long id);
    ControlDeCalidad findByOrdenDeTrabajoMaquinaId(Long id);

    @Override
    @EntityGraph(attributePaths = {"usuario", "medidasDeCalidad"})
    List<ControlDeCalidad> findAll(Specification<ControlDeCalidad> spec);

    @Query("SELECT new ar.utn.ccaffa.model.dto.metrics.ControlDeCalidadMetricsTotalByEstado(e.estado, COUNT(e)) FROM ControlDeCalidad e WHERE e.fechaControl >= :fechaControlDesde  GROUP BY e.estado")
    List<ControlDeCalidadMetricsTotalByEstado> totalByEstado(@Param("fechaControlDesde") LocalDateTime fechaControlDesde);

}