package com.projetfilrougeapi.apifilrouge.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantListRequest {
    @Positive(message = "Users ID must be positive number.")
    private List<Long> userIds;
}
