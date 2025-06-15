package com.projetfilrougeapi.apifilrouge.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A summary DTO for an event organizer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerSummary {
    private String pseudo;
    private String lastName;
    private String firstName;
    private String imageUrl;
    private Double note;
}
