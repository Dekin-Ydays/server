package com.projetfilrougeapi.apifilrouge.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Double totalPrice;
    private Long eventId;
    private Long userId;
    private int ticketToBeCreated;
    //private List<Long> ticketIds;
}
