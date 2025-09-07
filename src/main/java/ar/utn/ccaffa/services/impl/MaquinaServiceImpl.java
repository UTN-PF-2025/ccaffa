package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.mapper.impl.MaquinaMapperImpl;
import ar.utn.ccaffa.mapper.interfaces.MaquinaMapper;
import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.repository.interfaces.MaquinaRepository;
import ar.utn.ccaffa.services.interfaces.MaquinaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class MaquinaServiceImpl implements MaquinaService {
    
    private final MaquinaRepository maquinaRepository;
    private final MaquinaMapper maquinaMapper;

    public MaquinaServiceImpl(MaquinaRepository maquinaRepository, MaquinaMapperImpl maquinaMapper) {
        this.maquinaRepository = maquinaRepository;
        this.maquinaMapper = maquinaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaDto> findAll() {
        log.info("Obteniendo todas las máquinas");
        return this.maquinaMapper.toDtoList(maquinaRepository.findAll());
    }

    @Override
    public MaquinaDto findById(Long id) {
        log.info("Buscando máquina por ID: {}", id);
        return this.maquinaMapper.toDto(maquinaRepository.findById(id).orElse(Maquina.builder().build()));
    }

    @Override
    public Maquina save(MaquinaDto maquina) {
        log.info("Guardando nuevo maquina: {}", maquina);
        return maquinaRepository.save(this.maquinaMapper.toEntity(maquina));
    }

    @Override
    public boolean deleteById(Long id) {
        log.info("Eliminando maquina con ID: {}", id);
        if (!maquinaRepository.existsById(id)) {
            log.warn("No se encontró el maquina con ID: {}", id);
            return false;
        }
        maquinaRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Maquina> findAllIDsOfAvailableMachinesEntity(){
        return  maquinaRepository.findByActivoIsTrue();
    }

    @Override
    public List<Maquina> findByIdIn(List<Long> ids){
        return  maquinaRepository.findByIdIn(ids);
    }
}
