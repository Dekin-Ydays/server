package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.OrderRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.Ticket;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.TicketRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderForInvitationService {
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;

    public CreateOrderForInvitationService(OrderRepository orderRepository, TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
    }

    public Long createOrderForInvitation(Invitation invitation) {
        // Create a new order based on the invitation details
        Order order = new Order();
        order.setEvent(invitation.getEvent());
        order.setUser(invitation.getUser());
        order.setTotalPrice(invitation.getEvent().getPrice());
        // Set other necessary fields for the order

        // Save the order to the repository
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

    public Long createTicket(Long idOrder) {
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + idOrder));
        Ticket ticket = new Ticket();
        ticket.setOrder(order);
        ticket.setName(order.getEvent().getName());
        ticket.setDescription(order.getEvent().getDescription());
        ticket.setUnitPrice(order.getEvent().getPrice());
        Ticket savedTicket = ticketRepository.save(ticket);
        order.getTickets().add(ticket);
        return savedTicket.getId();
    }

}