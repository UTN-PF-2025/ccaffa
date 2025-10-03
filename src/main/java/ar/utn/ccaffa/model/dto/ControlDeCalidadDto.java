package ar.utn.ccaffa.model.dto;

import ar.utn.ccaffa.enums.EstadoControlDeCalidadEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControlDeCalidadDto {
    private Long id;
    private EmpleadoDto empleado;
    private LocalDate fechaControl;
    private Float espesorMedio;
    private Float anchoMedio;
    private Float rebabaMedio;
    private EstadoControlDeCalidadEnum estado;
    private List<MedidaDeCalidadDto> medidasDeCalidad;
    private CertificadoDeCalidadDto certificadoDeCalidad;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificadoDeCalidadDto {
        private Long id;
        private String numeroDeCertificado;
        private LocalDate fechaDeEmision;
        private EmpleadoDto aprobador;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpleadoDto {
        private Long id;
        private String nombre;
    }
} 