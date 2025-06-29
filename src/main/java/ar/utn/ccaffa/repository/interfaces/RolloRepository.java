package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.Rollo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RolloRepository extends JpaRepository<Rollo, Long>, JpaSpecificationExecutor<Rollo> {
}