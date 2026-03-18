package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Cargo;
import com.squad20.sistema_climbe.dto.CargoDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.mapper.CargoMapper;
import com.squad20.sistema_climbe.repository.CargoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;
    private final CargoMapper cargoMapper;

    @Transactional(readOnly = true)
    public Page<CargoDTO> findAll(Pageable pageable) {
        return cargoRepository.findAll(pageable).map(cargoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CargoDTO findById(Long id) {
        Cargo role = findRoleOrThrow(id);
        return cargoMapper.toDTO(role);
    }

    @Transactional
    public CargoDTO save(CargoDTO dto) {
        Cargo role = cargoMapper.toEntity(dto);
        role = cargoRepository.save(role);
        return cargoMapper.toDTO(role);
    }

    @Transactional
    public CargoDTO update(Long id, CargoDTO dto) {
        Cargo role = findRoleOrThrow(id);
        if (dto.getName() != null) role.setName(dto.getName());
        role = cargoRepository.save(role);
        return cargoMapper.toDTO(role);
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
}
