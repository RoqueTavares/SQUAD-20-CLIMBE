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

    public List<CargoDTO> findAll() {
        return cargoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public CargoDTO findById(Long id) {
        Cargo role = findRoleOrThrow(id);
        return toDTO(role);
    }

    public CargoDTO save(CargoDTO dto) {
        Cargo role = toEntity(dto);
        role = cargoRepository.save(role);
        return toDTO(role);
    }

    public CargoDTO update(Long id, CargoDTO dto) {
        Cargo role = findRoleOrThrow(id);
        role.setName(dto.getName());
        role = cargoRepository.save(role);
        return toDTO(role);
    }

    public void delete(Long id) {
        Cargo role = findRoleOrThrow(id);
        cargoRepository.delete(role);
    }

    private Cargo findRoleOrThrow(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo não encontrado com id: " + id));
    }

    private CargoDTO toDTO(Cargo role) {
        return CargoDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    private Cargo toEntity(CargoDTO dto) {
        return Cargo.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
