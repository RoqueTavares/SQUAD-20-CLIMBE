package com.squad20.sistema_climbe.dto;

import com.squad20.sistema_climbe.entity.User;
import lombok.Data;

@Data
public class UserDTO {

    private int id;
    private String nomeCompleto;
    private String cargo;
    private String cpf;
    private String email;
    private String contato;
    private String situacao;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.nomeCompleto = user.getNomeCompleto();
        this.cargo = user.getCargoId();
        this.cpf = user.getCpf();
        this.email = user.getEmail();
        this.contato = user.getContato();
        this.situacao = user.getSituacao();
    }
}
