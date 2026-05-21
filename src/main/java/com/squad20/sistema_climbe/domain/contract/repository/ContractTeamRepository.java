package com.squad20.sistema_climbe.domain.contract.repository;

import com.squad20.sistema_climbe.domain.contract.entity.ContractTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractTeamRepository extends JpaRepository<ContractTeam, Long> {
    List<ContractTeam> findByContract_Id(Long contractId);
}
