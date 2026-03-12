package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.dto.UserDTO;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
            userDTO.setNomeCompleto(user.get().getNomeCompleto());
            userDTO.setEmail(user.get().getEmail());
            userDTO.setCpf(user.get().getCpf());
            userDTO.setCargo(user.get().getCargoId());
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
            userDTO.setNomeCompleto(user.get().getNomeCompleto());
            userDTO.setEmail(user.get().getEmail());
            userDTO.setCpf(user.get().getCpf());
            userDTO.setCargo(user.get().getCargoId());
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
            userDTO.setNomeCompleto(user.get().getNomeCompleto());
            userDTO.setEmail(user.get().getEmail());
            userDTO.setCpf(user.get().getCpf());
            userDTO.setCargo(user.get().getCargoId());

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


        userManaged.setNomeCompleto(user.getNomeCompleto());
        userManaged.setEmail(user.getEmail());
        userManaged.setCpf(user.getCpf());
        userManaged.setCargoId(user.getCargoId());


        return userRepository.save(userManaged);
    }
}

