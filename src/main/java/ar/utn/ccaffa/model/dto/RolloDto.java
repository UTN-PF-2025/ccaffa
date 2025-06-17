package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolloDto {
    private Long id;
    private Long proveedorId;
    private String codigoProveedor;
    private Float peso;
    private Float ancho;
    private Float espesor;
    private String tipoMaterial;
    private String estado;
    private LocalDate fechaIngreso;
    private RolloDto rollo_padre;
}