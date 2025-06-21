package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.mapper.ProveedorMapper;
import ar.utn.ccaffa.model.dto.ProveedorDto;
import ar.utn.ccaffa.model.entity.Proveedor;
import ar.utn.ccaffa.repository.interfaces.ProveedorRepository;
import ar.utn.ccaffa.services.interfaces.ProveedorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository, ProveedorMapper proveedorMapper) {
        this.proveedorRepository = proveedorRepository;
        this.proveedorMapper = proveedorMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDto> findAll() {
        log.info("Buscando todos los proveedores");
        return this.proveedorMapper.toDtoList(proveedorRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorDto findById(Long id) {
        log.info("Buscando proveedor por ID: {}", id);
        return this.proveedorMapper.toDto(proveedorRepository.findById(id).orElse(Proveedor.builder().build()));
    }

    @Override
    public Proveedor save(ProveedorDto proveedor) {
        log.info("Guardando nuevo proveedor: {}", proveedor);
        return proveedorRepository.save(this.proveedorMapper.toEntity(proveedor));
    }

    @Override
    public boolean deleteById(Long id) {
        log.info("Eliminando proveedor con ID: {}", id);
        if (!proveedorRepository.existsById(id)) {
            log.warn("No se encontró el proveedor con ID: {}", id);
            return false;
        }
        proveedorRepository.deleteById(id);
        return true;
    }
}