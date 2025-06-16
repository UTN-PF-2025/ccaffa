package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.entity.Rol;
import java.util.List;

public interface RolService {
    List<Rol> findAll();
    Rol findById(Long id);
    Rol save(Rol rol);
    void deleteById(Long id);
} 