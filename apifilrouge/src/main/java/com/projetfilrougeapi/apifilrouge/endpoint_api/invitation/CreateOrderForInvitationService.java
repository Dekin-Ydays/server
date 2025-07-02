package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderForInvitationService {
    private final OrderRepository orderRepository;

    public CreateOrderForInvitationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void createOrderForInvitation(Invitation invitation) {
        if (invitation.getStatus() == Status.ACCEPTED) {
            // Create a new order based on the invitation details
            Order order = new Order();
            order.setEvent(invitation.getEvent());
            order.setUser(invitation.getUser());
            order.setTotalPrice(invitation.getEvent().getPrice());
            // Set other necessary fields for the order

            // Save the order to the repository
            orderRepository.save(order);
        }
    }
}
