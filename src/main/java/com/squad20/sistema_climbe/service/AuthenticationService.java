package com.squad20.sistema_climbe.service;
import com.squad20.sistema_climbe.entity.Cargo;
import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.dto.AuthenticationRequest;
import com.squad20.sistema_climbe.dto.AuthenticationResponse;
import com.squad20.sistema_climbe.dto.RegisterRequest;
import com.squad20.sistema_climbe.exception.ConflictException;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.CargoRepository;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final CargoRepository cargoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Este e-mail já está em uso.");
        }
        if (repository.findByCpf(request.getCpf()).isPresent()) {
            throw new ConflictException("Este CPF já está cadastrado.");
        }

        Cargo role = cargoRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado com id: " + request.getRoleId()));

        var user = User.builder()
                .fullName(request.getFullName())
                .cpf(request.getCpf())
                .phone(request.getPhone())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        repository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticationException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
