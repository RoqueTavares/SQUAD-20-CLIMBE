package com.squad20.sistema_climbe.entityDTO;

import com.squad20.sistema_climbe.entity.User;
import lombok.Data;

@Data
public class UserDTO {

    private int id;
    private String nome_completo;
    private String cargo;
    private String cpf;
    private String email;
    private String contato;
    private String situacao;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.nome_completo = user.getNome_completo();
        this.cargo = user.getCargo();
        this.cpf = user.getCpf();
        this.email = user.getEmail();
        this.contato = user.getContato();
        this.situacao = user.getSituacao();
    }
}
