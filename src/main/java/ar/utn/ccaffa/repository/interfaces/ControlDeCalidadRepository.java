package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlDeCalidadRepository extends JpaRepository<ControlDeCalidad, Long> {
} 