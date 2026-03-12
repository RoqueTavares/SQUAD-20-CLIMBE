package com.squad20.sistema_climbe.entityDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String nomeCompleto;
    private Long cargoId;
    private String cargoNome;
    private String cpf;
    private String email;
    private String contato;
    private String situacao;
}
