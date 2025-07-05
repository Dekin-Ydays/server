package com.projetfilrougeapi.apifilrouge.endpoint_api.order;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.Ticket;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public CollectionModel<EntityModel<OrderResponse>> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public EntityModel<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public EntityModel<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @PatchMapping("/{id}")
    public EntityModel<OrderResponse> updateOrder(@PathVariable Long id, OrderRequest request) {
        return orderService.updateOrder(id, request);
    }

    @DeleteMapping("/{id}")
    public EntityModel<Order> deleteOrder(@PathVariable Long id) {
        return orderService.deleteOrder(id);
    }

    @GetMapping("{id}/user")
    public EntityModel<UserResponse> getUserByOrderId(@PathVariable Long id) {
        return orderService.getUserByOrderId(id);
    }

    @GetMapping("/{id}/tickets")
    public CollectionModel<EntityModel<Ticket>> getTicketsByOrderId(@PathVariable Long id) {
        return orderService.getTicketsByOrderId(id);
    }

    @PostMapping("/{id}/tickets")
    public EntityModel<Ticket> addTicketToOrder(@PathVariable Long id, @Valid @RequestBody TicketRequest ticket) {
        return orderService.addTicketToOrder(id, ticket);
    }

    @GetMapping("{id}/events")
    public CollectionModel<EntityModel<EventResponse>> getEventsByOrderId(@PathVariable Long id) {
        return orderService.getEventsByOrderId(id);
    }

}
