package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequest {

    @NotNull(message = "An event ID is required.")
    @Positive(message = "Event ID must be a positive number.")
    private Long eventId;

    @NotNull(message = "A user ID is required.")
    @Positive(message = "User ID must be a positive number.")
    private Long userId;

    @NotNull(message = "A status is required for the invitation.")
    private Status status;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;
}

