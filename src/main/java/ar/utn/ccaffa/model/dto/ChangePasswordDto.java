package ar.utn.ccaffa.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {
    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    private String newPassword;
    @NotBlank(message = "La contraseña actual no puede estar vacía")
    private String oldPassword;
}
