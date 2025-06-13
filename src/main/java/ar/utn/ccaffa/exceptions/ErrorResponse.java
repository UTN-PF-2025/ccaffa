package ar.utn.ccaffa.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String status;
    private String message;
    private String path;

}