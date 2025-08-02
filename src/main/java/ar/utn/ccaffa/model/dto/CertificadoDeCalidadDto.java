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
public class CertificadoDeCalidadDto {
    private Long id;
    private String numeroDeCertificado;
    private LocalDate fechaDeEmision;
    private EmpleadoDto aprobador;
} 