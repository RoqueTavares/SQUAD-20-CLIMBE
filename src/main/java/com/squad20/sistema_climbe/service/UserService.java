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

    public List<UserDTO> listarTodos() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO buscarPorId(Long id) {
        User user = buscarUsuario(id);
        return toDTO(user);
    }

    public UserDTO buscarPorEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com e-mail: " + email));
        return toDTO(user);
    }

    public UserDTO buscarPorCpf(String cpf) {
        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com CPF: " + cpf));
        return toDTO(user);
    }

    public List<UserDTO> buscarPorCargo(Long cargoId) {
        return userRepository.findByCargo_Id(cargoId).stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO salvar(UserDTO dto) {
        validarUnicidadeEmailCpf(dto.getEmail(), dto.getCpf(), null);
        Cargo cargo = buscarCargo(dto.getCargoId());
        User user = toEntity(dto, cargo);
        user = userRepository.save(user);
        return toDTO(user);
    }

    public UserDTO atualizar(Long id, UserDTO dto) {
        User user = buscarUsuario(id);
        validarUnicidadeEmailCpf(dto.getEmail(), dto.getCpf(), id);
        Cargo cargo = buscarCargo(dto.getCargoId());
        user.setNomeCompleto(dto.getNomeCompleto());
        user.setCargo(cargo);
        user.setCpf(dto.getCpf());
        user.setEmail(dto.getEmail());
        user.setContato(dto.getContato());
        user.setSituacao(dto.getSituacao());
        user = userRepository.save(user);
        return toDTO(user);
    }

    public void excluir(Long id) {
        User user = buscarUsuario(id);
        userRepository.delete(user);
    }

    private User buscarUsuario(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    private Cargo buscarCargo(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo não encontrado com id: " + id));
    }

    private void validarUnicidadeEmailCpf(String email, String cpf, Long idUsuarioIgnorar) {
        if (emailEmUsoPorOutro(email, idUsuarioIgnorar)) {
            throw new RuntimeException("Já existe usuário cadastrado com este e-mail");
        }
        if (cpfEmUsoPorOutro(cpf, idUsuarioIgnorar)) {
            throw new RuntimeException("Já existe usuário cadastrado com este CPF");
        }
    }

    private boolean emailEmUsoPorOutro(String email, Long idIgnorar) {
        return userRepository.findByEmail(email)
                .map(u -> !u.getId().equals(idIgnorar))
                .orElse(false);
    }

    private boolean cpfEmUsoPorOutro(String cpf, Long idIgnorar) {
        return userRepository.findByCpf(cpf)
                .map(u -> !u.getId().equals(idIgnorar))
                .orElse(false);
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .nomeCompleto(user.getNomeCompleto())
                .cargoId(user.getCargo().getId())
                .cargoNome(user.getCargo().getNome())
                .cpf(user.getCpf())
                .email(user.getEmail())
                .contato(user.getContato())
                .situacao(user.getSituacao())
                .build();
    }

    private User toEntity(UserDTO dto, Cargo cargo) {
        return User.builder()
                .nomeCompleto(dto.getNomeCompleto())
                .cargo(cargo)
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .contato(dto.getContato())
                .situacao(dto.getSituacao())
                .senhaHash(null)
                .build();
    }
}
