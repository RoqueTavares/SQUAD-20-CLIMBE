package com.squad20.sistema_climbe.repository;

import com.squad20.sistema_climbe.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_Id(Long userId);
}
