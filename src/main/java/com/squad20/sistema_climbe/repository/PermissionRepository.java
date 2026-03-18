package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}

