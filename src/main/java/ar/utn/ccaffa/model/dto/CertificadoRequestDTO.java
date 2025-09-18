package ar.utn.ccaffa.model.dto;

import lombok.Data;

@Data
public class CertificadoRequestDTO {
    private Long controlDeCalidadId;
    private Long ordenDeTrabajoId;
    private EmpleadoDto aprobador;
    private String titulo;
    private String fecha;
    private String cliente;
    private String nroOrdenTrabajo;
    private String partida1;
    private String partida2;
    private String partida3;
    private String partida4;
    private String cantidadOriginal;
    private String anchoOriginal;
    private String espesorOriginal;
    private String durezaOriginal;
    private String resistencia;
    private String errorAnchoOriginal;
    private String errorEspesorOriginal;
    private String errorDurezaOriginal;
    private String cantidadReal;
    private String anchoReal;
    private String espesorReal;
    private String durezaReal;
    private String errorAnchoReal;
    private String errorEspesorReal;
    private String errorDurezaReal;
    private String composicionCarbono;
    private String composicionManganeso;
    private String composicionFosforo;
    private String composicionAzufre;
    private String composicionAluminio;
    private String composicionSilicio;


}
