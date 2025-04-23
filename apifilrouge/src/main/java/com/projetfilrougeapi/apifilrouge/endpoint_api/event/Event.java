package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.fasterxml.jackson.annotation.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.user.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long event_id;
    private LocalDateTime eventDate;
    private String description;
    private String eventName;
    private String address;
    private Integer maxCustomers;
    private Boolean isTrending;
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "place_id")
    @JsonBackReference(value = "place-events")
    private Place place;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "invitation-events")
    private List<Invitation> invitations;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-events")
    private User user;

}
