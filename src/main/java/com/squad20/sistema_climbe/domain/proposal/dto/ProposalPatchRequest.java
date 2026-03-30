package com.squad20.sistema_climbe.domain.proposal.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalPatchRequest {

    private Long enterpriseId;

    private Long userId;

    @Size(max = 50)
    private String status;
}
