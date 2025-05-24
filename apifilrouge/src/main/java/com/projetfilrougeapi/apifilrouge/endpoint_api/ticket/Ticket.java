package com.projetfilrougeapi.apifilrouge.endpoint_api.ticket;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;
    private String description;
    private Double unit_price;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference(value = "order-tickets")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

}
