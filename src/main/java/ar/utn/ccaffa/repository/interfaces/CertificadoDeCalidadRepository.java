package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.CertificadoDeCalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificadoDeCalidadRepository extends JpaRepository<CertificadoDeCalidad, Long> {
    Optional<CertificadoDeCalidad> findByControlDeCalidadId(Long controlDeCalidadId);
}
