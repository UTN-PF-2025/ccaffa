package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.MedidaDeCalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedidaDeCalidadRepository extends JpaRepository<MedidaDeCalidad, Long> {
}
