package com.squad20.sistema_climbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 255)
    private String fullName;

    @NotNull(message = "Cargo é obrigatório")
    private Long roleId;
    private String roleName;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF inválido")
    @Size(min = 11, max = 14)
    private String cpf;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 255)
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(max = 255)
    private String status;
}
