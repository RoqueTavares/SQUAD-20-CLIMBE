package com.squad20.sistema_climbe.dto;

import com.squad20.sistema_climbe.entity.User;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String fullName;
    private String role;
    private String cpf;
    private String email;
    private String phone;
    private String status;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.role = user.getRole() != null ? user.getRole().getName() : null;
        this.cpf = user.getCpf();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.status = user.getStatus();
    }
}
