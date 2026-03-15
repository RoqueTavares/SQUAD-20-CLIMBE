package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Optional<Enterprise> findByEmail(String email);
    Optional<Enterprise> findByCnpj(String cnpj);
}
