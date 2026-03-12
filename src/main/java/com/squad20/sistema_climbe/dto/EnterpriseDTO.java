package com.squad20.sistema_climbe.entityDTO;

import com.squad20.sistema_climbe.entity.Enterprise;
import lombok.Data;

@Data
public class EnterpriseDTO {

    private int id;

    private String razao_social;
    private String nome_fantasia;
    private String cnpj;
    private String logradouro;
    private int numero;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String telefone;
    private String email;
    private String representante_nome;
    private String representante_cnpj;
    private String representante_contato;

    public EnterpriseDTO() {}

    public EnterpriseDTO(Enterprise enterprise) {
        this.id = enterprise.getId();
        this.razao_social = enterprise.getRazao_social();
        this.nome_fantasia = enterprise.getNome_fantasia();
        this.cnpj = enterprise.getCnpj();
        this.logradouro = enterprise.getLogradouro();
        this.numero = enterprise.getNumero();
        this.bairro = enterprise.getBairro();
        this.cidade = enterprise.getCidade();
        this.uf = enterprise.getUf();
        this.cep = enterprise.getCep();
        this.telefone = enterprise.getTelefone();
        this.email = enterprise.getEmail();
        this.representante_nome = enterprise.getRepresentante_nome();
        this.representante_cnpj = enterprise.getRepresentante_cnpj();
        this.representante_contato = enterprise.getRepresentante_contato();
    }


}
