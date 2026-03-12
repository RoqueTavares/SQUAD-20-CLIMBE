package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Cargo;
import com.squad20.sistema_climbe.entityDTO.CargoDTO;
import com.squad20.sistema_climbe.repository.CargoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;

    public List<CargoDTO> listarTodos() {
        return cargoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public CargoDTO buscarPorId(Long id) {
        Cargo cargo = buscarCargo(id);
        return toDTO(cargo);
    }

    public CargoDTO salvar(CargoDTO dto) {
        Cargo cargo = toEntity(dto);
        cargo = cargoRepository.save(cargo);
        return toDTO(cargo);
    }

    public CargoDTO atualizar(Long id, CargoDTO dto) {
        Cargo cargo = buscarCargo(id);
        cargo.setNome(dto.getNome());
        cargo = cargoRepository.save(cargo);
        return toDTO(cargo);
    }

    public void excluir(Long id) {
        Cargo cargo = buscarCargo(id);
        cargoRepository.delete(cargo);
    }

    private Cargo buscarCargo(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo não encontrado com id: " + id));
    }

    private CargoDTO toDTO(Cargo cargo) {
        return CargoDTO.builder()
                .id(cargo.getId())
                .nome(cargo.getNome())
                .build();
    }

    private Cargo toEntity(CargoDTO dto) {
        return Cargo.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .build();
    }
}
