package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Cargo;
import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.entityDTO.UserDTO;
import com.squad20.sistema_climbe.repository.CargoRepository;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO findById(Long id) {
        User user = findUserOrThrow(id);
        return toDTO(user);
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com e-mail: " + email));
        return toDTO(user);
    }

    public UserDTO findByCpf(String cpf) {
        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com CPF: " + cpf));
        return toDTO(user);
    }

    public List<UserDTO> findByRoleId(Long roleId) {
        return userRepository.findByRole_Id(roleId).stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO save(UserDTO dto) {
        validateEmailCpfUnique(dto.getEmail(), dto.getCpf(), null);
        Cargo role = findRoleOrThrow(dto.getRoleId());
        User user = toEntity(dto, role);
        user = userRepository.save(user);
        return toDTO(user);
    }

    public UserDTO update(Long id, UserDTO dto) {
        User user = findUserOrThrow(id);
        validateEmailCpfUnique(dto.getEmail(), dto.getCpf(), id);
        Cargo role = findRoleOrThrow(dto.getRoleId());
        user.setFullName(dto.getFullName());
        user.setRole(role);
        user.setCpf(dto.getCpf());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus());
        user = userRepository.save(user);
        return toDTO(user);
    }

    public void delete(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    private Cargo findRoleOrThrow(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo não encontrado com id: " + id));
    }

    private void validateEmailCpfUnique(String email, String cpf, Long excludeUserId) {
        if (isEmailUsedByOther(email, excludeUserId)) {
            throw new RuntimeException("Já existe usuário cadastrado com este e-mail");
        }
        if (isCpfUsedByOther(cpf, excludeUserId)) {
            throw new RuntimeException("Já existe usuário cadastrado com este CPF");
        }
    }

    private boolean isEmailUsedByOther(String email, Long excludeUserId) {
        return userRepository.findByEmail(email)
                .map(u -> !u.getId().equals(excludeUserId))
                .orElse(false);
    }

    private boolean isCpfUsedByOther(String cpf, Long excludeUserId) {
        return userRepository.findByCpf(cpf)
                .map(u -> !u.getId().equals(excludeUserId))
                .orElse(false);
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .cpf(user.getCpf())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .build();
    }

    private User toEntity(UserDTO dto, Cargo role) {
        return User.builder()
                .fullName(dto.getFullName())
                .role(role)
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .status(dto.getStatus())
                .passwordHash(null)
                .build();
    }
}
