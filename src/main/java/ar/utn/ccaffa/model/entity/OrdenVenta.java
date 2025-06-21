package ar.utn.ccaffa.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Orden_Venta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    @NotNull(message = "El número de orden es obligatorio")
    private Long orderId;

    @Column(name = "fecha_creacion", nullable = false)
    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;

    @Column(name = "estado", nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private String estado;

    @Column(name = "observaciones")
    private String observaciones;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "orden_venta_id")
    private List<Especificacion> especificaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_trabajo_id")
    private OrdenDeTrabajo ordenDeTrabajo;
}