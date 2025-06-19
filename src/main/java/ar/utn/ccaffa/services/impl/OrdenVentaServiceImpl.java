package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.mapper.interfaces.OrdenVentaMapper;
import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.repository.interfaces.DefectoRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenVentaServiceImpl implements OrdenVentaService {
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenVentaMapper ordenVentaMapper;
    private final DefectoRepository defectoRepository;

    public OrdenVentaServiceImpl(OrdenVentaRepository ordenVentaRepository, OrdenVentaMapper ordenVentaMapper, DefectoRepository defectoRepository) {
        this.ordenVentaRepository = ordenVentaRepository;
        this.ordenVentaMapper = ordenVentaMapper;
        this.defectoRepository = defectoRepository;
    }

    @Override
    public List<OrdenVentaDto> findAll() {
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findAll());
    }

    @Override
    public OrdenVentaDto findById(Long id) {
        return this.ordenVentaMapper.toDto(this.ordenVentaRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Orden de venta", "id", id)));
    }

    @Override
    public OrdenVenta save(OrdenVentaDto ordenVenta) {
        return this.ordenVentaRepository.save(this.ordenVentaMapper.toEntity(ordenVenta));
    }

    @Override
    public void deleteById(Long id) {
        this.ordenVentaRepository.deleteById(id);
    }

    @Override
    public Defecto obtenerDefectoPorId(Long id) {
        return this.defectoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Defecto", "id", id));
    }

    @Override
    public void crearDefecto(Defecto defecto) {
        this.defectoRepository.save(defecto);
    }
}
