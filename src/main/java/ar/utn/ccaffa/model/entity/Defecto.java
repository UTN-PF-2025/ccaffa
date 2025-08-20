package ar.utn.ccaffa.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Defecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "imagen_ubicacion")
    private String imagen;
    @Column(name = "fecha")
    private LocalDate fecha;
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "es_rechazado")
    private Boolean esRechazado;

    @ManyToOne
    @JoinColumn(name = "control_de_calidad_id")
    @JsonBackReference
    private ControlDeCalidad controlDeCalidad;
}
