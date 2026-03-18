package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByEnterprise_Id(Long enterpriseId);
}
