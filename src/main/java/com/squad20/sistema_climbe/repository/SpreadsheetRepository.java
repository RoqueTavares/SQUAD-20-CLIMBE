package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.Spreadsheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpreadsheetRepository extends JpaRepository<Spreadsheet, Long> {

    List<Spreadsheet> findByContract_Id(Long contractId);
}
