package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.Rollo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RolloRepository extends JpaRepository<Rollo, Long> {
    List<Rollo> findByRolloPadreId(Long rolloPadreId);
}