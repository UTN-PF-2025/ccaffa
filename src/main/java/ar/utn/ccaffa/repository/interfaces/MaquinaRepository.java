package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Long> {
    
    @Query("SELECT m FROM Maquina m WHERE m.id = :id")
    @NonNull Optional<Maquina> findById(@NonNull @Param("id") Long id);
} 