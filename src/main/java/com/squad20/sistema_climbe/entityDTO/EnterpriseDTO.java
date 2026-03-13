package com.squad20.sistema_climbe.entityDTO;

import com.squad20.sistema_climbe.entity.Enterprise;
import lombok.Data;

@Data
public class EnterpriseDTO {

    private int id;
    private String legalName;
    private String tradeName;
    private String cnpj;
    private String street;
    private int number;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String phone;
    private String email;
    private String representativeName;
    private String representativeCpf;
    private String representativePhone;

    public EnterpriseDTO() {}

    public EnterpriseDTO(Enterprise enterprise) {
        this.id = enterprise.getId();
        this.legalName = enterprise.getLegalName();
        this.tradeName = enterprise.getTradeName();
        this.cnpj = enterprise.getCnpj();
        this.street = enterprise.getStreet();
        this.number = enterprise.getNumber();
        this.neighborhood = enterprise.getNeighborhood();
        this.city = enterprise.getCity();
        this.state = enterprise.getState();
        this.zipCode = enterprise.getZipCode();
        this.phone = enterprise.getPhone();
        this.email = enterprise.getEmail();
        this.representativeName = enterprise.getRepresentativeName();
        this.representativeCpf = enterprise.getRepresentativeCpf();
        this.representativePhone = enterprise.getRepresentativePhone();
    }
}
