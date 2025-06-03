package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.ProveedorDto;
import ar.utn.ccaffa.model.entity.Proveedor;

import java.util.List;

public interface ProveedorService {
    List<ProveedorDto> findAll();
    ProveedorDto findById(Long id);
    Proveedor save(ProveedorDto proveedor);
    boolean deleteById(Long id);
}
