package com.projetfilrougeapi.apifilrouge.endpoint_api.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.Ticket;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="total_price")
    private Double totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "order-tickets")
    private List<Ticket> tickets;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", nullable = false)
    @JsonBackReference(value = "orders-event")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-orders")
    private User user;


}
