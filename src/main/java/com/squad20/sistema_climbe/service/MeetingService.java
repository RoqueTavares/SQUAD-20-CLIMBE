package com.squad20.sistema_climbe.service;

import com.squad20.sistema_climbe.entity.Enterprise;
import com.squad20.sistema_climbe.entity.Meeting;
import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.entityDTO.MeetingDTO;
import com.squad20.sistema_climbe.exception.ResourceNotFoundException;
import com.squad20.sistema_climbe.repository.EnterpriseRepository;
import com.squad20.sistema_climbe.repository.MeetingRepository;
import com.squad20.sistema_climbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MeetingDTO> findAll() {
        return meetingRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeetingDTO> findByEnterpriseId(Long enterpriseId) {
        return meetingRepository.findByEnterprise_Id(enterpriseId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeetingDTO findById(Long id) {
        Meeting meeting = findMeetingOrThrow(id);
        return toDTO(meeting);
    }

    @Transactional
    public MeetingDTO save(MeetingDTO dto) {
        Enterprise enterprise = enterpriseRepository.findById(dto.getEnterpriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + dto.getEnterpriseId()));
        Set<User> participants = resolveParticipants(dto.getParticipantIds());
        Meeting meeting = toEntity(dto, enterprise, participants);
        meeting = meetingRepository.save(meeting);
        return toDTO(meeting);
    }

    @Transactional
    public MeetingDTO update(Long id, MeetingDTO dto) {
        Meeting existing = findMeetingOrThrow(id);
        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getDate() != null) existing.setDate(dto.getDate());
        if (dto.getTime() != null) existing.setTime(dto.getTime());
        if (dto.getInPerson() != null) existing.setInPerson(dto.getInPerson());
        if (dto.getLocation() != null) existing.setLocation(dto.getLocation());
        if (dto.getAgenda() != null) existing.setAgenda(dto.getAgenda());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getEnterpriseId() != null) {
            Enterprise enterprise = enterpriseRepository.findById(dto.getEnterpriseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + dto.getEnterpriseId()));
            existing.setEnterprise(enterprise);
        }
        if (dto.getParticipantIds() != null) {
            existing.setParticipants(resolveParticipants(dto.getParticipantIds()));
        }
        existing = meetingRepository.save(existing);
        return toDTO(existing);
    }

    @Transactional
    public void delete(Long id) {
        Meeting meeting = findMeetingOrThrow(id);
        meetingRepository.delete(meeting);
    }

    private Meeting findMeetingOrThrow(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reunião não encontrada com id: " + id));
    }

    private Set<User> resolveParticipants(List<Long> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<User> users = new HashSet<>();
        for (Long userId : participantIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + userId));
            users.add(user);
        }
        return users;
    }

    private MeetingDTO toDTO(Meeting m) {
        Long enterpriseId = null;
        String enterpriseName = null;
        if (m.getEnterprise() != null) {
            enterpriseId = m.getEnterprise().getId();
            enterpriseName = m.getEnterprise().getTradeName() != null
                    ? m.getEnterprise().getTradeName() : m.getEnterprise().getLegalName();
        }
        List<Long> participantIds = m.getParticipants() == null
                ? List.of()
                : m.getParticipants().stream().map(User::getId).toList();
        return MeetingDTO.builder()
                .id(m.getId())
                .enterpriseId(enterpriseId)
                .enterpriseName(enterpriseName)
                .title(m.getTitle())
                .date(m.getDate())
                .time(m.getTime())
                .inPerson(m.getInPerson())
                .location(m.getLocation())
                .agenda(m.getAgenda())
                .status(m.getStatus())
                .participantIds(participantIds)
                .build();
    }

    private Meeting toEntity(MeetingDTO dto, Enterprise enterprise, Set<User> participants) {
        return Meeting.builder()
                .title(dto.getTitle())
                .enterprise(enterprise)
                .date(dto.getDate())
                .time(dto.getTime())
                .inPerson(dto.getInPerson())
                .location(dto.getLocation())
                .agenda(dto.getAgenda())
                .status(dto.getStatus())
                .participants(participants != null ? participants : new HashSet<>())
                .build();
    }
}
