package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.entity.Usuario;
import ar.utn.ccaffa.model.entity.Rol;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.repository.interfaces.RolRepository;
import ar.utn.ccaffa.repository.interfaces.UsuarioRepository;
import ar.utn.ccaffa.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public Usuario save(Usuario usuario) {
        // Primero guardamos los roles si no existen
        if (usuario.getRoles() != null) {
            Set<Rol> rolesGuardados = usuario.getRoles().stream()
                .map(rol -> {
                    if (rol.getId() == null) {
                        return rolRepository.save(rol);
                    }
                    return rol;
                })
                .collect(java.util.stream.Collectors.toSet());
            usuario.setRoles(rolesGuardados);
        }
        
        // Luego guardamos el usuario
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
} 