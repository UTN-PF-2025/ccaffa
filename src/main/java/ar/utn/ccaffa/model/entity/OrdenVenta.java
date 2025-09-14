package ar.utn.ccaffa.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Orden_Venta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"ordenDeTrabajo"})
@NamedEntityGraph(
    name = "OrdenVenta.withEspecificacion",
    attributeNodes = @NamedAttributeNode("especificacion")
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_creacion", nullable = false)
    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "estado", nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private String estado;

    @Column(name = "observaciones")
    private String observaciones;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "especificacion_id")
    private Especificacion especificacion;

}