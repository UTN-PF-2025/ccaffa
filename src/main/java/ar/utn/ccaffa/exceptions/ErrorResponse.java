package ar.utn.ccaffa.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Clase DTO para respuestas de error estandarizadas.
 * Incluye detalles útiles para el cliente (API consumers).
 */
@Data
@Builder
public class ErrorResponse {
    private String status;
    private String message;
    private String path;

}