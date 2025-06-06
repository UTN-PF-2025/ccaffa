package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.entity.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> findAll();
    Usuario findById(Long id);
    Usuario findByUsername(String username);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
} 