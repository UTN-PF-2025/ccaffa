package ar.utn.ccaffa.repository;

import ar.utn.ccaffa.model.entity.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Long> {
    
    @Query("SELECT m FROM Maquina m WHERE m.id = :id")
    Optional<Maquina> findById(@Param("id") Long id);
} 