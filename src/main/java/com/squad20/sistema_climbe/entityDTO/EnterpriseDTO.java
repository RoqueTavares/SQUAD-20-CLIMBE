package com.squad20.sistema_climbe.entityDTO;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnterpriseDTO {

    private Long id;

    @Size(max = 255)
    private String legalName;

    @Size(max = 255)
    private String tradeName;

    @Size(max = 18)
    private String cnpj;

    @Size(max = 255)
    private String street;

    @Size(max = 255)
    private String number;

    @Size(max = 255)
    private String neighborhood;

    @Size(max = 255)
    private String city;

    @Size(max = 2)
    private String state;

    @Size(max = 10)
    private String zipCode;

    @Size(max = 50)
    private String phone;

    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String representativeName;

    @Size(max = 14)
    private String representativeCpf;

    @Size(max = 50)
    private String representativePhone;
}
