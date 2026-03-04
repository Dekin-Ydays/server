package com.projetfilrougeapi.apifilrouge.endpoint_api.subscription;

import com.projetfilrougeapi.apifilrouge.DTO.SubscriptionResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public void subscribe(Long subscriberId, Long subscribedId) {

        if (subscriberId.equals(subscribedId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot subscribe to oneself"
            );
        }

        if (subscriptionRepository
                .findBySubscriberIdAndSubscribedId(subscriberId, subscribedId)
                .isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Subscription already exists"
            );
        }

        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Subscriber not found"));

        User subscribed = userRepository.findById(subscribedId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Subscription subscription = Subscription.builder()
                .subscriber(subscriber)
                .subscribed(subscribed)
                .build();

        subscriptionRepository.save(subscription);
    }

    public void unsubscribe(Long subscriberId, Long subscribedId) {

        Subscription subscription = subscriptionRepository
                .findBySubscriberIdAndSubscribedId(subscriberId, subscribedId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Subscription not found"));

        subscriptionRepository.delete(subscription);
    }

    public CollectionModel<EntityModel<SubscriptionResponse>> getSubscriptions(Long userId) {

        List<EntityModel<SubscriptionResponse>> subscriptions = subscriptionRepository
                .findBySubscriberId(userId)
                .stream()
                .map(subscription -> {
                    SubscriptionResponse response = SubscriptionResponse.fromEntity(subscription);
                    return EntityModel.of(
                            response,
                            linkTo(methodOn(SubscriptionController.class)
                                    .getSubscriptionById(subscription.getId()))
                                    .withSelfRel()
                    );
                })
                .collect(Collectors.toList());

        return CollectionModel.of(
                subscriptions,
                linkTo(methodOn(SubscriptionController.class)
                        .getSubscriptions(userId))
                        .withSelfRel()
        );
    }

    public CollectionModel<EntityModel<SubscriptionResponse>> getFollowers(Long userId) {

        List<EntityModel<SubscriptionResponse>> followers = subscriptionRepository
                .findBySubscribedId(userId)
                .stream()
                .map(subscription -> {
                    SubscriptionResponse response = SubscriptionResponse.fromEntity(subscription);
                    return EntityModel.of(
                            response,
                            linkTo(methodOn(SubscriptionController.class)
                                    .getSubscriptionById(subscription.getId()))
                                    .withSelfRel()
                    );
                })
                .collect(Collectors.toList());

        return CollectionModel.of(
                followers,
                linkTo(methodOn(SubscriptionController.class)
                        .getFollowers(userId))
                        .withSelfRel()
        );
    }

    public EntityModel<SubscriptionResponse> getSubscriptionById(Long id) {

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Subscription not found"));

        SubscriptionResponse response = SubscriptionResponse.fromEntity(subscription);

        return EntityModel.of(
                response,
                linkTo(methodOn(SubscriptionController.class)
                        .getSubscriptionById(id))
                        .withSelfRel()
        );
    }
}
