package ar.utn.ccaffa.model.dto;

import lombok.Data;

@Data
public class CertificadoRequestDTO {
    private Long controlDeCalidadId;
    private String partida1;
    private String partida2;
    private String partida3;
    private String partida4;
    private String cantidadOriginal;
    private String durezaOriginal;
    private String errorDurezaOriginal;

}
