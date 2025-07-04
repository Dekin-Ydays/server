package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse extends RepresentationModel<OrderResponse> {
    private Long id;
    private Double totalPrice;
    private int ticketToBeCreated;
    private List<TicketResponse> tickets;


    public static OrderResponse fromEntity(Order order) {
        List<TicketResponse> ticketResponses = order.getTickets() != null
                ? order.getTickets().stream()
                .map(ticket -> TicketResponse.builder()
                        .id(ticket.getId())
                        .name(ticket.getName())
                        .lastName(ticket.getLastName())
                        .description(ticket.getDescription())
                        .unitPrice(ticket.getUnitPrice())
                        .build())
                .collect(Collectors.toList())
                : List.of();

        return OrderResponse.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .ticketToBeCreated(order.getTicketToBeCreated())
                .tickets(ticketResponses)
                .build();
    }

}