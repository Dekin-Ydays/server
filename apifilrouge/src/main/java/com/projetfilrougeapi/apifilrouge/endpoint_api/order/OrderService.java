package com.projetfilrougeapi.apifilrouge.endpoint_api.order;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class OrderService {
    OrderRepository orderRepository;
    OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public EntityModel<Order> getCommandeById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel());
    }
    public EntityModel<Order> createCommande(Order order) {
        Order savedOrder = orderRepository.save(order);
        return EntityModel.of(savedOrder,
                linkTo(methodOn(OrderController.class).getOrderById(savedOrder.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("commandes"));
    }

    public EntityModel<Order> updateCommande(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        existingOrder.setPrixTotal(order.getPrixTotal());
        Order updatedOrder = orderRepository.save(existingOrder);

        return EntityModel.of(updatedOrder,
                linkTo(methodOn(OrderController.class).getOrderById(updatedOrder.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("commandes"));
    }

    public CollectionModel<EntityModel<Order>> getAllCommandes() {
        List<EntityModel<Order>> commandes = orderRepository.findAll().stream()
                .map(order -> EntityModel.of(order,
                        linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(commandes,
                linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
    }

    public EntityModel<Order> deleteCommande(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        orderRepository.delete(order);

        return EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("commandes"));
    }
}
