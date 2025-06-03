package ar.utn.ccaffa.repository;

import ar.utn.ccaffa.model.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
