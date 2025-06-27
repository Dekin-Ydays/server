package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="invitation_id", nullable = false, updatable = false, unique = true)
    private Long id;

    private String description;

    /*@Enumerated(EnumType.STRING)
    private Type type;*/

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference(value = "invitation-events")
    @JsonIgnore
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-invitations")
    @JsonIgnore
    private User user;
}
