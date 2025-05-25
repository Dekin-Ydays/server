package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.projetfilrougeapi.apifilrouge.endpoint_api.city.City;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", nullable = false, updatable = false, unique = true)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String address;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "place-events")
    private List<Event> events;

    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    @JsonIgnore
    private City city;

}
