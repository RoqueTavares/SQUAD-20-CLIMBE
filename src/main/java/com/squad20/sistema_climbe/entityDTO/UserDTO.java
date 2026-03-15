package com.squad20.sistema_climbe.entityDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @Size(max = 255)
    private String fullName;

    private Long roleId;
    private String roleName;

    @Size(min = 11, max = 14)
    private String cpf;

    @Email(message = "E-mail inválido")
    @Size(max = 255)
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(max = 255)
    private String status;
}
