// package ar.utn.ccaffa.services.impl;

// import ar.utn.ccaffa.mapper.interfaces.CamaraMapper;
// import ar.utn.ccaffa.model.dto.CamaraDto;
// import ar.utn.ccaffa.model.entity.Camara;
// import ar.utn.ccaffa.repository.interfaces.CamaraRepository;
// import ar.utn.ccaffa.services.interfaces.CamaraService;
// import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import java.util.List;

// @Service
// @RequiredArgsConstructor
// public class CamaraServiceImpl implements CamaraService {

//     private final CamaraRepository camaraRepository;
//     private final CamaraMapper camaraMapper;

//     @Override
//     public CamaraDto save(CamaraDto camaraDto) {
//         Camara camara = camaraMapper.toEntity(camaraDto);
//         camara.setIsDeleted(false);
//         return camaraMapper.toDto(camaraRepository.save(camara));
//     }

//     @Override
//     public CamaraDto findById(String id) {
//         Camara camara = camaraRepository.findByIdAndIsDeletedFalse(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Camara", "id", id));
//         return camaraMapper.toDto(camara);
//     }

//     @Override
//     public List<CamaraDto> findAll() {
//         return camaraMapper.toDtoList(camaraRepository.findByIsDeletedFalse());
//     }

//     @Override
//     public CamaraDto update(String id, CamaraDto camaraDto) {
//         Camara existingCamara = camaraRepository.findByIdAndIsDeletedFalse(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Camara", "id", id));
        
//         existingCamara.setNombre(camaraDto.getNombre());
//         existingCamara.setDescripcion(camaraDto.getDescripcion());
//         existingCamara.setUrl(camaraDto.getUrl());
//         existingCamara.setUbicacion(camaraDto.getUbicacion());

//         return camaraMapper.toDto(camaraRepository.save(existingCamara));
//     }

//     @Override
//     public void deleteById(String id) {
//         camaraRepository.findByIdAndIsDeletedFalse(id).ifPresent(camara -> {
//             camara.setIsDeleted(true);
//             camaraRepository.save(camara);
//         });
//     }
// } 