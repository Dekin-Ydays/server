package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.fasterxml.jackson.annotation.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
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
    @Column(name = "event_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="event_date")
    private LocalDateTime date;
    private String description;
    private String name;
    private String address;
    @Column(name="max_customers")
    private Integer maxCustomers;
    @Column(name="is_trending")
    private Boolean isTrending;
    private Double price;
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ManyToOne
    @JoinColumn(name = "place_id")
    @JsonBackReference(value = "place-events")
    private Place place;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "invitation-events")
    private List<Invitation> invitations;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-events")
    private User user;

    @ManyToMany
    @JoinTable(name = "event_category",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonIgnoreProperties("events")
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "event-orders")
    private List<Order> orders;
}
