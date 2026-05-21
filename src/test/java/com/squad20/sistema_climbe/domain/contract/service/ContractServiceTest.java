package com.squad20.sistema_climbe.domain.contract.service;

import com.squad20.sistema_climbe.domain.contract.entity.Contract;
import com.squad20.sistema_climbe.domain.contract.entity.ContractTeam;
import com.squad20.sistema_climbe.domain.contract.mapper.ContractMapper;
import com.squad20.sistema_climbe.domain.contract.repository.ContractRepository;
import com.squad20.sistema_climbe.domain.contract.repository.ContractTeamRepository;
import com.squad20.sistema_climbe.domain.proposal.entity.Proposal;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.domain.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock private ContractRepository contractRepository;
    @Mock private ContractTeamRepository contractTeamRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private ContractMapper contractMapper;

    @InjectMocks private ContractService service;

    @Test
    void testSetExecutionDeadline() {
        Contract contract = new Contract();
        contract.setId(1L);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any())).thenReturn(contract);

        service.setExecutionDeadline(1L, LocalDate.of(2026, 12, 31));

        verify(contractRepository).save(argThat(c -> c.getExecutionDeadline().equals(LocalDate.of(2026, 12, 31))));
    }

    @Test
    void testAssignTeam() {
        Contract contract = new Contract();
        contract.setId(1L);
        
        User user = new User();
        user.setId(5L);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        service.assignTeam(1L, List.of(5L), "LEAD");

        verify(contractTeamRepository).saveAll(anyList());
        verify(notificationService).save(any());
    }
}
