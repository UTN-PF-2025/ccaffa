package ar.utn.ccaffa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.utn.ccaffa.model.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}