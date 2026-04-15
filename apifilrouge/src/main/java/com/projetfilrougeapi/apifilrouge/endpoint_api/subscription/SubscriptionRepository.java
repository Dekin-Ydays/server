package com.projetfilrougeapi.apifilrouge.endpoint_api.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "subscriptions", path = "subscriptions")
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findBySubscriberIdAndSubscribedId(Long subscriberId, Long subscribedId);

    List<Subscription> findBySubscriberId(Long subscriberId);
    List<Subscription> findBySubscribedId(Long subscribedId);
}