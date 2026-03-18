package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByContract_Id(Long contractId);
}
