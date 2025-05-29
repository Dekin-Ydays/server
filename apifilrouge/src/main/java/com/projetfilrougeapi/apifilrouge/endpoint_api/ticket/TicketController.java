package com.projetfilrougeapi.apifilrouge.endpoint_api.ticket;

import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public EntityModel<Ticket> createTicket(Ticket ticket) {
        return ticketService.createTicket(ticket);
    }

    @GetMapping
    public CollectionModel<EntityModel<Ticket>> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public EntityModel<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }
    /*@GetMapping("/{id}/order")
    public EntityModel<Order> getOrdersForTicket(Long id) {
        return ticketService.getOrdersForTicket(id);
    }*/

    @PatchMapping
    public EntityModel<Ticket> updateTicket(@PathVariable Long id, Ticket ticket) {
        return ticketService.updateTicket(id, ticket);
    }
    @DeleteMapping("/{id}")
    public void deleteTicket(Long id) {
        ticketService.deleteTicket(id);
    }

}