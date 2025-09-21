package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ControlDeCalidadRepository extends JpaRepository<ControlDeCalidad, Long> {
    @Query("SELECT c FROM ControlDeCalidad c LEFT JOIN FETCH c.medidasDeCalidad WHERE c.id = :id")
    Optional<ControlDeCalidad> findByIdWithMedidas(@Param("id") Long id);

    @Query("SELECT c FROM ControlDeCalidad c LEFT JOIN FETCH c.medidasDeCalidad WHERE c.ordenDeTrabajoId = :ordenDeTrabajoId")
    List<ControlDeCalidad> findByOrdenDeTrabajoId(Long ordenDeTrabajoId);
}