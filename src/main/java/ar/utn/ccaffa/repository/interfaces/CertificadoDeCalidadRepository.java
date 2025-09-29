package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.CertificadoDeCalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificadoDeCalidadRepository extends JpaRepository<CertificadoDeCalidad, Long> {
}
