package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.mapper.ProveedorMapper;
import ar.utn.ccaffa.mapper.RolloMapper;
import ar.utn.ccaffa.model.dto.ProveedorDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.entity.Proveedor;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.repository.interfaces.ProveedorRepository;
import ar.utn.ccaffa.repository.interfaces.RolloRepository;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class RolloServiceImpl implements RolloService {

    private final RolloRepository rolloRepository;
    private final RolloMapper rolloMapper;

    public RolloServiceImpl(RolloRepository rolloRepository, RolloMapper rolloMapper) {
        this.rolloRepository = rolloRepository;
        this.rolloMapper = rolloMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolloDto> findAll() {
        log.info("Buscando todos los rollos");
        return this.rolloMapper.toDtoList(rolloRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public RolloDto findById(Long id) {
        log.info("Buscando rollo por ID: {}", id);
        return this.rolloMapper.toDto(rolloRepository.findById(id).orElse(Rollo.builder().build()));
    }

    @Override
    public Rollo save(RolloDto rollo) {
        log.info("Guardando nuevo rollo: {}", rollo);
        return rolloRepository.save(this.rolloMapper.toEntity(rollo));
    }

    @Override
    public boolean deleteById(Long id) {
        log.info("Eliminando rollo con ID: {}", id);
        if (!rolloRepository.existsById(id)) {
            log.warn("No se encontró el rollo con ID: {}", id);
            return false;
        }
        rolloRepository.deleteById(id);
        return true;
    }
}