package com.projetfilrougeapi.apifilrouge.endpoint_api.ticket;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ticket {
    @Id
    private Long ticketId;
    private String name;
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference(value = "ticket-order")
    private Order order;

}
