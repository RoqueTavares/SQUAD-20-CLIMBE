package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Notification;
import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.entityDTO.NotificationDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.NotificationRepository;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationDTO> findAll() {
        return notificationRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> findByUserId(Long userId) {
        return notificationRepository.findByUser_Id(userId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationDTO findById(Long id) {
        Notification notification = findNotificationOrThrow(id);
        return toDTO(notification);
    }

    @Transactional
    public NotificationDTO save(NotificationDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + dto.getUserId()));
        Notification notification = toEntity(dto, user);
        if (notification.getSentAt() == null) {
            notification.setSentAt(LocalDateTime.now());
        }
        notification = notificationRepository.save(notification);
        return toDTO(notification);
    }

    @Transactional
    public NotificationDTO update(Long id, NotificationDTO dto) {
        Notification existing = findNotificationOrThrow(id);
        if (dto.getMessage() != null) existing.setMessage(dto.getMessage());
        if (dto.getSentAt() != null) existing.setSentAt(dto.getSentAt());
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + dto.getUserId()));
            existing.setUser(user);
        }
        existing = notificationRepository.save(existing);
        return toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Notification notification = findNotificationOrThrow(id);
        notificationRepository.delete(notification);
    }

    private Notification findNotificationOrThrow(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada com id: " + id));
    }

    private NotificationDTO toDTO(Notification n) {
        Long userId = n.getUser() != null ? n.getUser().getId() : null;
        String userName = n.getUser() != null ? n.getUser().getFullName() : null;
        return NotificationDTO.builder()
                .id(n.getId())
                .userId(userId)
                .userName(userName)
                .message(n.getMessage())
                .sentAt(n.getSentAt())
                .type(n.getType())
                .build();
    }

    private Notification toEntity(NotificationDTO dto, User user) {
        return Notification.builder()
                .user(user)
                .message(dto.getMessage())
                .sentAt(dto.getSentAt())
                .type(dto.getType())
                .build();
    }
}
