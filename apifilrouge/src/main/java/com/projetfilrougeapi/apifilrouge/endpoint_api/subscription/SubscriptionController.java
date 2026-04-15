package com.projetfilrougeapi.apifilrouge.endpoint_api.subscription;

import com.projetfilrougeapi.apifilrouge.DTO.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscribes")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{subscriberId}/subscribe/{subscribedId}")
    public void subscribe(
            @PathVariable Long subscriberId,
            @PathVariable Long subscribedId
    ) {
        subscriptionService.subscribe(subscriberId, subscribedId);
    }

    @DeleteMapping("/{subscriberId}/unsubscribe/{subscribedId}")
    public void unsubscribe(
            @PathVariable Long subscriberId,
            @PathVariable Long subscribedId
    ) {
        subscriptionService.unsubscribe(subscriberId, subscribedId);
    }

    @GetMapping("/{userId}/subscriptions")
    public CollectionModel<EntityModel<SubscriptionResponse>> getSubscriptions(
            @PathVariable Long userId
    ) {
        return subscriptionService.getSubscriptions(userId);
    }

    @GetMapping("{userId}/followers")
    public CollectionModel<EntityModel<SubscriptionResponse>> getFollowers(
            @PathVariable Long userId
    ) {
        return subscriptionService.getFollowers(userId);
    }

    @GetMapping("/{id}")
    public EntityModel<SubscriptionResponse> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id);
    }
}