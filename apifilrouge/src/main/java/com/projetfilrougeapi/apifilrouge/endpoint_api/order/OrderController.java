package com.projetfilrougeapi.apifilrouge.endpoint_api.order;

import com.projetfilrougeapi.apifilrouge.DTO.OrderRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.Ticket;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping
    public CollectionModel<EntityModel<Order>> getAllOrders() {
        return orderService.getAllOrders();
    }
    @GetMapping("/{id}")
    public EntityModel<Order> getOrderById(Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public EntityModel<Order> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @PatchMapping("/{id}")
    public EntityModel<Order> updateOrder(Long id, Order order) {
        return orderService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public EntityModel<Order> deleteOrder(Long id) {
        return orderService.deleteOrder(id);
    }

    @GetMapping("{id}/user")
    public EntityModel<User> getUserByOrderId(@PathVariable Long id) {
        return orderService.getUserByOrderId(id);
    }
    @GetMapping("/{id}/tickets")
    public CollectionModel<EntityModel<Ticket>> getTicketsByOrderId(@PathVariable Long id) {
        return orderService.getTicketsByOrderId(id);}
}
