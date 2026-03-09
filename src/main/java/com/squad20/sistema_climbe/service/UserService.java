package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.entityDTO.UserDTO;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> findAll() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(user -> new UserDTO(user))
                .toList();

        if (users.isEmpty()) {
            throw new RuntimeException("No users found");
        }

        return users;
    }

    public UserDTO findById(int id) {
        Optional<User> user = userRepository.findById(id);

        UserDTO userDTO = new UserDTO();
        if (user.isPresent()) {
            userDTO.setId(user.get().getId());
            userDTO.setNome_completo(user.get().getNome_completo());
            userDTO.setEmail(user.get().getEmail());
            userDTO.setCpf(user.get().getCpf());
            userDTO.setCargo(user.get().getCargo());
        }else{
            throw new EmptyResultDataAccessException(1);
        }

        return userDTO;
    }

    public UserDTO findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        UserDTO userDTO = new UserDTO();
        if (user.isPresent()) {
            userDTO.setId(user.get().getId());
            userDTO.setNome_completo(user.get().getNome_completo());
            userDTO.setEmail(user.get().getEmail());
            userDTO.setCpf(user.get().getCpf());
            userDTO.setCargo(user.get().getCargo());
        }else{
            throw new EmptyResultDataAccessException(1);
        }

        return userDTO;
    }

    public UserDTO findByCpf(String cpf) {
        Optional<User> user = userRepository.findByCpf(cpf);

        UserDTO userDTO = new UserDTO();

        if (user.isPresent()) {
            userDTO.setId(user.get().getId());
            userDTO.setNome_completo(user.get().getNome_completo());
            userDTO.setEmail(user.get().getEmail());
            userDTO.setCpf(user.get().getCpf());
            userDTO.setCargo(user.get().getCargo());

        }else {
            throw new EmptyResultDataAccessException(1);
        }

        return userDTO;
    }

    public List<UserDTO> findByCargo(String cargo) {
        return userRepository.findByCargo(cargo).stream()
                .map(UserDTO::new)
                .toList();
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(User user, int id) {

        User userManaged = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));


        userManaged.setNome_completo(user.getNome_completo());
        userManaged.setEmail(user.getEmail());
        userManaged.setCpf(user.getCpf());
        userManaged.setCargo(user.getCargo());


        return userRepository.save(userManaged);
    }
}

