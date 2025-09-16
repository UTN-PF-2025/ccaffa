// package ar.utn.ccaffa.mapper.impl;

// import ar.utn.ccaffa.mapper.interfaces.CamaraMapper;
// import ar.utn.ccaffa.model.dto.CamaraDto;
// import ar.utn.ccaffa.model.entity.Camara;
// import org.springframework.stereotype.Component;

// import java.util.Collections;
// import java.util.List;
// import java.util.stream.Collectors;

// @Component
// public class CamaraMapperImpl implements CamaraMapper {

//     @Override
//     public CamaraDto toDto(Camara camara) {
//         if (camara == null) {
//             return null;
//         }
//         return new CamaraDto(
//                 camara.getId(),
//                 camara.getNombre(),
//                 camara.getDescripcion(),
//                 camara.getUrl(),
//                 camara.getUbicacion()
//         );
//     }

//     @Override
//     public Camara toEntity(CamaraDto camaraDto) {
//         if (camaraDto == null) {
//             return null;
//         }
//         return Camara.builder()
//                 .id(camaraDto.getId())
//                 .nombre(camaraDto.getNombre())
//                 .descripcion(camaraDto.getDescripcion())
//                 .url(camaraDto.getUrl())
//                 .ubicacion(camaraDto.getUbicacion())
//                 .build();
//     }

//     @Override
//     public List<CamaraDto> toDtoList(List<Camara> camaras) {
//         if (camaras == null || camaras.isEmpty()) {
//             return Collections.emptyList();
//         }
//         return camaras.stream()
//                 .map(this::toDto)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     public List<Camara> toEntityList(List<CamaraDto> camaraDtos) {
//         if (camaraDtos == null || camaraDtos.isEmpty()) {
//             return Collections.emptyList();
//         }
//         return camaraDtos.stream()
//                 .map(this::toEntity)
//                 .collect(Collectors.toList());
//     }
// } 