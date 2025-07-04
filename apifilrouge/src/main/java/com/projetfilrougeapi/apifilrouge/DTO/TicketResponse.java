package com.projetfilrougeapi.apifilrouge.DTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String name;
    private String lastName;
    private String description;
    private Double unitPrice;
}
