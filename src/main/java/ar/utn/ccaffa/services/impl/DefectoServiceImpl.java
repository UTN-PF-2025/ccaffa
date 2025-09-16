package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.dto.DefectoDto;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.repository.interfaces.DefectoRepository;
import ar.utn.ccaffa.services.interfaces.DefectoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefectoServiceImpl implements DefectoService {

    private final DefectoRepository defectoRepository;

    @Override
    public DefectoDto findById(Long id) {
        Defecto defecto = defectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Defecto no encontrado con ID: " + id));
        return toDto(defecto);
    }

    @Override
    public DefectoDto actualizarEstadoRechazo(Long id, boolean esRechazado) {
        Defecto defecto = defectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Defecto no encontrado con ID: " + id));
        defecto.setEsRechazado(esRechazado);
        Defecto actualizado = defectoRepository.save(defecto);
        return toDto(actualizado);
    }

    @Override
    public DefectoDto actualizarEstadoRechazoPorImagen(String imageId, boolean esRechazado, String camaraId) {
        String imagen = camaraId + "/" + imageId;
        Defecto defecto = defectoRepository.findByImagen(imagen)
                .orElseThrow(() -> new RuntimeException("Defecto no encontrado con imageId: " + imageId));
        defecto.setEsRechazado(esRechazado);
        Defecto actualizado = defectoRepository.save(defecto);
        return toDto(actualizado);
    }

    private DefectoDto toDto(Defecto defecto) {
        DefectoDto dto = new DefectoDto();
        dto.setId(defecto.getId());
        dto.setImagen(defecto.getImagen());
        dto.setFecha(defecto.getFecha());
        dto.setTipo(defecto.getTipo());
        dto.setDescripcion(defecto.getDescripcion());
        dto.setEsRechazado(defecto.getEsRechazado());
        if (defecto.getControlDeCalidad() != null) {
            dto.setControlDeCalidadId(defecto.getControlDeCalidad().getId());
        }
        return dto;
    }
}

