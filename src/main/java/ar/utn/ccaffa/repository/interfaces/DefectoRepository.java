package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.Defecto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DefectoRepository extends JpaRepository<Defecto, Long> {
    Optional<Defecto> findByImagen(String imagen);
}
