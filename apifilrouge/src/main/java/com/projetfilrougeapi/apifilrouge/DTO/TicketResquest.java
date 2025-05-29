package com.projetfilrougeapi.apifilrouge.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResquest {
    private String name;
    private String lastName;
    private String description;
    private Double unitPrice;
    private Long orderId;
}
