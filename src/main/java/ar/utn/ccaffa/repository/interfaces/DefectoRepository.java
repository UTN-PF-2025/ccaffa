package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.dto.metrics.DefectoMetricsTotalByRechazado;
import ar.utn.ccaffa.model.dto.metrics.OVMetricsTotalByEstado;
import ar.utn.ccaffa.model.entity.Defecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DefectoRepository extends JpaRepository<Defecto, Long> {
    Optional<Defecto> findByImagen(String imagen);

    @Query("SELECT new ar.utn.ccaffa.model.dto.metrics.DefectoMetricsTotalByRechazado(e.esRechazado, COUNT(e)) FROM Defecto e WHERE e.fecha >= :fechaCreacionDesde  GROUP BY e.esRechazado")
    List<DefectoMetricsTotalByRechazado> totalByRechazado(@Param("fechaCreacionDesde") LocalDate fechaCreacionDesde);
}
