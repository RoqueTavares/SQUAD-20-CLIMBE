package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.OfferedService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<OfferedService, Long> {
}

