package com.projetfilrougeapi.apifilrouge.endpoint_api.order;

import com.projetfilrougeapi.apifilrouge.DTO.OrderRequest;
import com.projetfilrougeapi.apifilrouge.DTO.TicketResquest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.Ticket;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.TicketController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.TicketRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class OrderService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    OrderRepository orderRepository;

    OrderService(OrderRepository orderRepository, EventRepository eventRepository, UserRepository userRepository, TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    public EntityModel<Order> getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(order.getId())).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getUserByOrderId(order.getId())).withRel("users"),
                linkTo(methodOn(OrderController.class).getEventsByOrderId(order.getId())).withRel("events")
        );
    }

    @Transactional
    public EntityModel<Order> createOrder(OrderRequest request) {
        //String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        //Object user = userRepository.findByEmail(username)
        //        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        Object event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event non trouvé"));

        Order order = new Order();
        //order.setUser((User) user); // Assuming user is of type User, adjust if necessary
        order.setUser(user);
        order.setTotalPrice(request.getTotalPrice());
        order.setEvent((Event) event);


        Order savedOrder = orderRepository.save(order);

        return EntityModel.of(savedOrder,
                linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(order.getId())).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getUserByOrderId(order.getId())).withRel("users"),
                linkTo(methodOn(OrderController.class).getEventsByOrderId(order.getId())).withRel("events")
        );

    }

    public EntityModel<Order> updateOrder(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        existingOrder.setTotalPrice(order.getTotalPrice());
        Order updatedOrder = orderRepository.save(existingOrder);

        return EntityModel.of(updatedOrder,
                linkTo(methodOn(OrderController.class).getOrderById(updatedOrder.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(updatedOrder.getId())).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getUserByOrderId(updatedOrder.getId())).withRel("users"),
                linkTo(methodOn(OrderController.class).getEventsByOrderId(order.getId())).withRel("events")
        );
    }

    public CollectionModel<EntityModel<Order>> getAllOrders() {
        List<EntityModel<Order>> orders = orderRepository.findAll().stream()
                .map(order -> EntityModel.of(order,
                        linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel(),
                linkTo(methodOn(TicketController.class).getAllTickets()).withRel("tickets"),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(EventController.class).getAllEvents(null, null, null, null, null)).withRel("events"));
    }

    public EntityModel<Order> deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        orderRepository.delete(order);

        return EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }


    public EntityModel<User> getUserByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        User user = order.getUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found for this order");
        }

        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getOrderById(id)).withRel("order"));

    }

    public CollectionModel<EntityModel<Ticket>> getTicketsByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        List<EntityModel<Ticket>> tickets = order.getTickets().stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(TicketController.class).getTicketById(ticket.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(tickets,
                linkTo(methodOn(OrderController.class).getOrderById(id)).withRel("order"));
    }

    public EntityModel<Ticket> addTicketToOrder(Long id, TicketResquest ticket) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Ticket newTicket = new Ticket();
        newTicket.setName(ticket.getName());
        newTicket.setLastName(ticket.getLastName());
        newTicket.setDescription(ticket.getDescription());
        newTicket.setUnit_price(ticket.getUnitPrice());
        newTicket.setOrder(order);

        Ticket savedTicket = ticketRepository.save(newTicket);
        order.getTickets().add(savedTicket);
        orderRepository.save(order);

        return EntityModel.of(savedTicket,
                linkTo(methodOn(TicketController.class).getTicketById(savedTicket.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getOrderById(id)).withRel("order"),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(order.getId())).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getUserByOrderId(order.getId())).withRel("users"),
                linkTo(methodOn(OrderController.class).getEventsByOrderId(order.getId())).withRel("events")
        );
    }

 public CollectionModel<EntityModel<Event>> getEventsByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Event event = order.getEvent();
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found for this order");
        }

        return CollectionModel.of(
                List.of(EntityModel.of(event,
                        linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel())),
                linkTo(methodOn(OrderController.class).getOrderById(id)).withRel("order"));
    }
}