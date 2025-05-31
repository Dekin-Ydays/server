package com.projetfilrougeapi.apifilrouge.DTO;

import lombok.Data;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantListRequest {
    private List<Long> userIds;
}
