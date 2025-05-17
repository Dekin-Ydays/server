package com.projetfilrougeapi.apifilrouge.endpoint_api.ticket;

import com.projetfilrougeapi.apifilrouge.endpoint_api.order.OrderController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public EntityModel<Ticket> getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(ticket,
                linkTo(methodOn(TicketController.class).getAllTickets()).withSelfRel(),
                linkTo(methodOn(TicketController.class).getAllTickets()).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }
    public EntityModel<Ticket> createTicket(Ticket ticket) {
        Ticket savedTicket = ticketRepository.save(ticket);

        return EntityModel.of(savedTicket,
                linkTo(methodOn(TicketController.class).getAllTickets()).withSelfRel(),
                linkTo(methodOn(TicketController.class).getAllTickets()).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }
    public EntityModel<Ticket> updateTicket(Long id, Ticket ticket) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        existingTicket.setName(ticket.getName());
        existingTicket.setLastName(ticket.getLastName());
        existingTicket.setOrder(ticket.getOrder());

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
                        linkTo(methodOn(TicketController.class).getTicketById(ticket.getTicketId())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(tickets,
                linkTo(methodOn(TicketController.class).getAllTickets()).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }
}
