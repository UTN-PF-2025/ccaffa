package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.Defecto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DefectoRepository extends MongoRepository<Defecto, Long> {
}
