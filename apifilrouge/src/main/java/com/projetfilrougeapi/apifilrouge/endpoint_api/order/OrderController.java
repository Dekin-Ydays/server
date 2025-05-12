package com.projetfilrougeapi.apifilrouge.endpoint_api.order;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commandes")
public class OrderController {
    OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping
    public CollectionModel<EntityModel<Order>> getAllCommandes() {
        return orderService.getAllCommandes();
    }
    @GetMapping("/{id}")
    public EntityModel<Order> getCommandeById(Long id) {
        return orderService.getCommandeById(id);
    }
    @PostMapping
    public EntityModel<Order> createCommande(Order order) {
        return orderService.createCommande(order);
    }
    @PatchMapping("/{id}")
    public EntityModel<Order> updateCommande(Long id, Order order) {
        return orderService.updateCommande(id, order);
    }
    @DeleteMapping("/{id}")
    public EntityModel<Order> deleteCommande(Long id) {
        return orderService.deleteCommande(id);
    }
}
