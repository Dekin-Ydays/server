package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.subscription.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private Long subscriberId;
    private String subscriberUsername;
    private Long subscribedId;
    private String subscribedUsername;

    public static SubscriptionResponse fromEntity(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .subscriberId(subscription.getSubscriber().getId())
                .subscriberUsername(subscription.getSubscriber().getUsername())
                .subscribedId(subscription.getSubscribed().getId())
                .subscribedUsername(subscription.getSubscribed().getUsername())
                .build();
    }
}
