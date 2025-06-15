package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "postal_code")
    private String postalCode;

    private String region;

    private Double latitude;

    private Double longitude;

    private String country;

    private String description;

    @Column(columnDefinition="TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    @JsonBackReference("place-city")
    private List<Place> places;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "city-events")
    private List<Event> events;

    @ManyToMany
    @JoinTable(
            name = "city_nearest_cities",
            joinColumns = @JoinColumn(name = "city_id"),
            inverseJoinColumns = @JoinColumn(name = "nearest_city_id"))
    @Builder.Default // par défaut initialiser a vide
    private Set<City> nearestCities = new HashSet<>();
}