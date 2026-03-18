package com.squad20.sistema_climbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoDTO {

    private Long id;

    @NotBlank(message = "Nome do cargo é obrigatório")
    @Size(max = 255)
    private String name;
}
