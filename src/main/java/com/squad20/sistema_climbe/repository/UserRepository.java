package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional <User> findByCpf(String cpf);
    //mudar quando adicionar a classe cargo
    List<User> findByCargoId(String cargo);
}
