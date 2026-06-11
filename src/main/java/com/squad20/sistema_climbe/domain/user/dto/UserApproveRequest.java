package com.squad20.sistema_climbe.domain.user.dto;

import com.squad20.sistema_climbe.domain.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApproveRequest {

    @Schema(description = "Cargo (Role) atribuído ao usuário na aprovação", example = "ANALISTA_SENIOR")
    @NotNull(message = "O cargo é obrigatório na aprovação")
    private Role role;
}
