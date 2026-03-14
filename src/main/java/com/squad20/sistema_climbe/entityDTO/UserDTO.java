package com.squad20.sistema_climbe.entityDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String fullName;
    private Long roleId;
    private String roleName;
    private String cpf;
    private String email;
    private String phone;
    private String status;
}
