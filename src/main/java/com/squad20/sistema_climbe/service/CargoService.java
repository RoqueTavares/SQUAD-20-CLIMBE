package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Cargo;
import com.squad20.sistema_climbe.entityDTO.CargoDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.CargoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;

    @Transactional(readOnly = true)
    public List<CargoDTO> findAll() {
        return cargoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CargoDTO findById(Long id) {
        Cargo role = findRoleOrThrow(id);
        return toDTO(role);
    }

    @Transactional
    public CargoDTO save(CargoDTO dto) {
        Cargo role = toEntity(dto);
        role = cargoRepository.save(role);
        return toDTO(role);
    }

    @Transactional
    public CargoDTO update(Long id, CargoDTO dto) {
        Cargo role = findRoleOrThrow(id);
        if (dto.getName() != null) role.setName(dto.getName());
        role = cargoRepository.save(role);
        return toDTO(role);
    }

    @Transactional
    public void delete(Long id) {
        Cargo role = findRoleOrThrow(id);
        cargoRepository.delete(role);
    }

    private Cargo findRoleOrThrow(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado com id: " + id));
    }

    private CargoDTO toDTO(Cargo role) {
        return CargoDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    private Cargo toEntity(CargoDTO dto) {
        return Cargo.builder()
                .name(dto.getName())
                .build();
    }
}
