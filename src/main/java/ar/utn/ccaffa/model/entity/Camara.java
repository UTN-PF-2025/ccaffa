package ar.utn.ccaffa.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "camaras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Camara {
    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String url;
    private String ubicacion;
    @Builder.Default
    private Boolean isDeleted = false;
} 