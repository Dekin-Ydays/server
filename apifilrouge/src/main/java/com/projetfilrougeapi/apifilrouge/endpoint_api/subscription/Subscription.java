package com.projetfilrougeapi.apifilrouge.endpoint_api.subscription;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"subscriber_id", "subscribed_id"}
        )
)
public class Subscription {

    //ID of to subscribe
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //User who is subscribes
    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    private User subscriber;

    //User who is subscribed to
    @ManyToOne
    @JoinColumn(name = "subscribed_id", nullable = false)
    private User subscribed;

}
