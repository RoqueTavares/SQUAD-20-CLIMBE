package com.squad20.sistema_climbe.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCompletionRequest {

    @Schema(description = "CPF do usuário", example = "12345678901")
    @NotBlank(message = "O CPF é obrigatório")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos")
    private String cpf;

    @Schema(description = "Telefone do usuário", example = "11999999999")
    @NotBlank(message = "O telefone é obrigatório")
    private String phone;
}
