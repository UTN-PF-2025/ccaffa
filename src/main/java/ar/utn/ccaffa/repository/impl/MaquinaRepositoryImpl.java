package ar.utn.ccaffa.repository.impl;

import ar.utn.ccaffa.model.entity.Maquina;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MaquinaRepositoryImpl {
    
    private static final Logger log = LoggerFactory.getLogger(MaquinaRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Maquina> findByIdCustom(Long id) {
        try {
            log.info("Buscando máquina con ID: {} en la base de datos", id);
            
            // Primero verificamos si hay datos en la tabla y mostramos el conteo
            String countQuery = "SELECT COUNT(m) FROM Maquina m";
            Long count = entityManager.createQuery(countQuery, Long.class)
                    .getSingleResult();
            log.info("Total de máquinas en la base de datos: {}", count);

            String findQuery = "SELECT m FROM Maquina m WHERE m.id = :id";
            List<Maquina> results = entityManager.createQuery(findQuery, Maquina.class)
                    .setParameter("id", id)
                    .getResultList();
            
            if (results.isEmpty()) {
                log.warn("No se encontró ninguna máquina con ID: {}", id);
                return Optional.empty();
            }
            
            Maquina maquina = results.get(0);
            return Optional.of(maquina);
            
        } catch (Exception e) {
            log.error("Error al buscar máquina con ID: {}. Error: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }
}