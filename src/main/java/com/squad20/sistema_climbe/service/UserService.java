package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Cargo;
import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.dto.UserDTO;
import com.squad20.sistema_climbe.exception.ConflictException;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.mapper.UserMapper;
import com.squad20.sistema_climbe.repository.CargoRepository;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = findUserOrThrow(id);
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com e-mail: " + email));
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO findByCpf(String cpf) {
        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com CPF: " + cpf));
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findByRoleId(Long roleId) {
        return userRepository.findByRole_Id(roleId).stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Transactional
    public UserDTO save(UserDTO dto) {
        validateEmailCpfUnique(dto.getEmail(), dto.getCpf(), null);
        Cargo role = findRoleOrThrow(dto.getRoleId());
        User user = userMapper.toEntity(dto);
        user.setRole(role);
        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User user = findUserOrThrow(id);
        validateEmailCpfUnique(dto.getEmail(), dto.getCpf(), id);
        if (dto.getRoleId() != null) {
            Cargo role = findRoleOrThrow(dto.getRoleId());
            user.setRole(role);
        }
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getCpf() != null) user.setCpf(dto.getCpf());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
    }

    private Cargo findRoleOrThrow(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado com id: " + id));
    }

    private void validateEmailCpfUnique(String email, String cpf, Long excludeUserId) {
        if (email != null && isEmailUsedByOther(email, excludeUserId)) {
            throw new ConflictException("Já existe usuário cadastrado com este e-mail");
        }
        if (cpf != null && isCpfUsedByOther(cpf, excludeUserId)) {
            throw new ConflictException("Já existe usuário cadastrado com este CPF");
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
}
