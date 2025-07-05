package com.projetfilrougeapi.apifilrouge.endpoint_api.order;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.Ticket;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.TicketController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.ticket.TicketRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.*;
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
    private final UserService userService;

    OrderRepository orderRepository;

    OrderService(OrderRepository orderRepository, EventRepository eventRepository, UserRepository userRepository, TicketRepository ticketRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.userService = userService;
    }

    public EntityModel<OrderResponse> getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        OrderResponse orderResponse = OrderResponse.fromEntity(order);

        return EntityModel.of(orderResponse,
                linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(order.getId())).withRel("tickets"), // Le lien vers la liste de tickets
                linkTo(methodOn(OrderController.class).getUserByOrderId(order.getId())).withRel("users"),
                linkTo(methodOn(OrderController.class).getEventsByOrderId(order.getId())).withRel("events")
        );
    }

    @Transactional
    public EntityModel<OrderResponse> createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event non trouvé"));
        if (event.getParticipants().size() + request.getTicketToBeCreated() > event.getMaxCustomers()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nombre de participants dépasse la limite autorisée pour cet événement");
        }
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(request.getTotalPrice());
        order.setEvent(event);
        order.setTicketToBeCreated(request.getTicketToBeCreated());


        Order savedOrder = orderRepository.save(order);

        OrderResponse orderResponse = OrderResponse.fromEntity(savedOrder);

        return EntityModel.of(orderResponse,
                linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(order.getId())).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getUserByOrderId(order.getId())).withRel("users"),
                linkTo(methodOn(OrderController.class).getEventsByOrderId(order.getId())).withRel("events")
        );

    }

    public EntityModel<OrderResponse> updateOrder(Long id, OrderRequest orderRequest) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String connectedEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Role currentRole = userService.getCurrentUserRole();

        if (currentRole != Role.Admin && currentRole != Role.AuthService && !existingOrder.getUser().getEmail().equals(connectedEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this order.");
        }

        if (orderRequest.getTotalPrice() != null) {
            existingOrder.setTotalPrice(orderRequest.getTotalPrice());
        }

        Order updatedOrder = orderRepository.save(existingOrder);

        OrderResponse orderResponse = OrderResponse.fromEntity(updatedOrder);

        return EntityModel.of(orderResponse,
                linkTo(methodOn(OrderController.class).getOrderById(updatedOrder.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(updatedOrder.getId())).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getUserByOrderId(updatedOrder.getId())).withRel("users"),
                linkTo(methodOn(OrderController.class).getEventsByOrderId(updatedOrder.getId())).withRel("events")
        );
    }


    public CollectionModel<EntityModel<OrderResponse>> getAllOrders() {
        List<EntityModel<OrderResponse>> orders = orderRepository.findAll().stream()
                .map(order -> {
                    OrderResponse orderResponse = OrderResponse.fromEntity(order);
                    return EntityModel.of(orderResponse,
                            linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
                            linkTo(methodOn(OrderController.class).getTicketsByOrderId(order.getId())).withRel("tickets"),
                            linkTo(methodOn(OrderController.class).getUserByOrderId(order.getId())).withRel("users"),
                            linkTo(methodOn(OrderController.class).getEventsByOrderId(order.getId())).withRel("events")
                    );
                })
                .collect(Collectors.toList());

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllOrganizers(null)).withRel("organizers"),
                linkTo(methodOn(EventController.class).getAllEvents(null, true, null, null, null, null, null, null, null)).withRel("events")
        );
    }

    public EntityModel<Order> deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String connectedEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Role currentRole = userService.getCurrentUserRole();

        if (currentRole != Role.Admin && currentRole != Role.AuthService && !order.getUser().getEmail().equals(connectedEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this order.");
        }

        orderRepository.delete(order);

        return EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
    }

    public EntityModel<UserResponse> getUserByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        User user = order.getUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found for this order");
        }
        UserResponse userResponse = new UserResponse().fromEntity(user);

        return EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(order.getId())).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getOrderById(id)).withRel("order"));

    }

    public CollectionModel<EntityModel<Ticket>> getTicketsByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        String connectedEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Role currentRole = userService.getCurrentUserRole();

        if (currentRole != Role.Admin && currentRole != Role.AuthService && !order.getUser().getEmail().equals(connectedEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access tickets for this order.");
        }

        List<EntityModel<Ticket>> tickets = order.getTickets().stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(TicketController.class).getTicketById(ticket.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(tickets,
                linkTo(methodOn(OrderController.class).getOrderById(id)).withRel("order"));
    }


    public EntityModel<Ticket> addTicketToOrder(Long id, TicketRequest ticket) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        String connectedEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Role currentRole = userService.getCurrentUserRole();

        if (currentRole != Role.Admin && currentRole != Role.AuthService && !order.getUser().getEmail().equals(connectedEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to add tickets for this order.");
        }

        Ticket newTicket = new Ticket();
        newTicket.setName(ticket.getName());
        newTicket.setLastName(ticket.getLastName());
        newTicket.setDescription(ticket.getDescription());
        newTicket.setUnitPrice(ticket.getUnitPrice());
        newTicket.setOrder(order);

        Ticket savedTicket = ticketRepository.save(newTicket);
        order.getTickets().add(savedTicket);
        orderRepository.save(order);

        order.getEvent().getParticipants().add(order.getUser());
        eventRepository.save(order.getEvent());

        return EntityModel.of(savedTicket,
                linkTo(methodOn(TicketController.class).getTicketById(savedTicket.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getOrderById(id)).withRel("order"),
                linkTo(methodOn(OrderController.class).getTicketsByOrderId(order.getId())).withRel("tickets"),
                linkTo(methodOn(OrderController.class).getUserByOrderId(order.getId())).withRel("users"),
                linkTo(methodOn(OrderController.class).getEventsByOrderId(order.getId())).withRel("events")
        );
    }


    public CollectionModel<EntityModel<EventResponse>> getEventsByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        String connectedEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Role currentRole = userService.getCurrentUserRole();

        if (currentRole != Role.Admin && currentRole != Role.AuthService && !order.getUser().getEmail().equals(connectedEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access the event for this order.");
        }

        Event event = order.getEvent();
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found for this order");
        }
        EventResponse eventResponse = new EventResponse().fromEntity(event);

        return CollectionModel.of(
                List.of(EntityModel.of(eventResponse,
                        linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel())),
                linkTo(methodOn(OrderController.class).getOrderById(id)).withRel("order")
        );
    }
}