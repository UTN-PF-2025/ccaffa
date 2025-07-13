package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.Camara;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CamaraRepository extends MongoRepository<Camara, String> {
    Optional<Camara> findByIdAndIsDeletedFalse(String id);
    List<Camara> findByIsDeletedFalse();
} 