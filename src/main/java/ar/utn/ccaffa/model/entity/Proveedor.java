package ar.utn.ccaffa.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Proveedor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

}
