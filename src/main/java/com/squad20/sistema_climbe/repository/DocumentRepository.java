package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByEnterprise_Id(Long enterpriseId);
    List<Document> findByAnalyst_Id(Long analystId);
}
