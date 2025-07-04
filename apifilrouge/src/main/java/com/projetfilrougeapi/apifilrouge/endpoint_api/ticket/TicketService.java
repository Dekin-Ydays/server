package com.projetfilrougeapi.apifilrouge.endpoint_api.ticket;

import com.projetfilrougeapi.apifilrouge.DTO.TicketRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.OrderController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.OrderRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    public TicketService(TicketRepository ticketRepository, OrderRepository orderRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public EntityModel<Ticket> getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(ticket,
                linkTo(methodOn(TicketController.class).getTicketById(id)).withSelfRel(),
                linkTo(methodOn(TicketController.class).getAllTickets()).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }

    public EntityModel<Ticket> createTicket(TicketRequest ticket) {
        Order order = orderRepository.findById(ticket.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Ticket newTicket = Ticket.builder()
                .name(ticket.getName())
                .description(ticket.getDescription())
                .unitPrice(ticket.getUnitPrice())
                .order(order)
                .build();
        Ticket savedTicket = ticketRepository.save(newTicket);
        // save the ticket in the order
        order.getTickets().add(savedTicket);
        orderRepository.save(order);

        Event event = eventRepository.findById(order.getEvent().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        event.getParticipants().add(user);
        eventRepository.save(event);

        return EntityModel.of(savedTicket,
                linkTo(methodOn(TicketController.class).getAllTickets()).withSelfRel(),
                linkTo(methodOn(TicketController.class).getAllTickets()).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }
    public EntityModel<Ticket> updateTicket(Long id, TicketRequest ticket) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (ticket.getName().isEmpty()) {
            existingTicket.setName(ticket.getName());
        }
        if (ticket.getLastName().isEmpty()) {
            existingTicket.setLastName(ticket.getLastName());
        }
        if (ticket.getUnitPrice() == null) {
            existingTicket.setUnitPrice(ticket.getUnitPrice());
        }
        if (ticket.getDescription().isEmpty()) {
            existingTicket.setDescription(ticket.getDescription());
        }

        Ticket updatedTicket = ticketRepository.save(existingTicket);

        return EntityModel.of(updatedTicket,
                linkTo(methodOn(TicketController.class).getAllTickets()).withSelfRel(),
                linkTo(methodOn(TicketController.class).getAllTickets()).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }
    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ticketRepository.delete(ticket);
    }

    public CollectionModel<EntityModel<Ticket>> getAllTickets() {
        List<EntityModel<Ticket>> tickets = ticketRepository.findAll().stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(TicketController.class).getTicketById(ticket.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(tickets,
                linkTo(methodOn(TicketController.class).getAllTickets()).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }
}