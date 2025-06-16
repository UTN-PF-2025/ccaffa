package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import ar.utn.ccaffa.mapper.MaquinaMapper;
import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.repository.MaquinaRepository;
import ar.utn.ccaffa.repository.impl.MaquinaRepositoryImpl;
import ar.utn.ccaffa.services.interfaces.MaquinaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class MaquinaServiceImpl implements MaquinaService {
    
    private final MaquinaRepository maquinaRepository;
    private final MaquinaRepositoryImpl maquinaRepositoryImpl;
    private final MaquinaMapper maquinaMapper;

    public MaquinaServiceImpl(MaquinaRepository maquinaRepository, MaquinaRepositoryImpl maquinaRepositoryImpl, MaquinaMapper maquinaMapper) {
        this.maquinaRepository = maquinaRepository;
        this.maquinaRepositoryImpl = maquinaRepositoryImpl;
        this.maquinaMapper = maquinaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaDto> obtenerTodos() {
        log.info("Obteniendo todas las máquinas");
        List<Maquina> maquinas = maquinaRepository.findAll();
        log.info("Se encontraron {} máquinas", maquinas.size());
        return maquinas.stream()
                .map(maquinaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MaquinaDto obtenerPorId(Long id) {
        log.info("Iniciando búsqueda de máquina con ID: {}", id);
        
        return maquinaRepositoryImpl.findByIdCustom(id)
                .map(maquina -> {
                    log.info("Máquina encontrada en la base de datos: {}", maquina);
                    return maquinaMapper.toDto(maquina);
                })
                .orElse(MaquinaDto.builder()
                        .id(9999)
                        .name("Maquina Mock")
                        .estado("Activa")
                        .type(MaquinaTipoEnum.CORTADORA)
                        .build()
                );

    }

    @Override
    @Transactional
    public MaquinaDto guardar(MaquinaDto dto) {
        log.info("Guardando máquina: {}", dto);
        Maquina entity = maquinaMapper.toEntity(dto);
        log.info("Entidad convertida: {}", entity);
        Maquina savedEntity = maquinaRepository.save(entity);
        log.info("Máquina guardada: {}", savedEntity);
        return maquinaMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando máquina con ID: {}", id);
        maquinaRepository.deleteById(id);
        log.info("Máquina eliminada correctamente");
    }
}
