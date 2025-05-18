package com.projetfilrougeapi.apifilrouge.endpoint_api.city;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(name = "city_name", nullable = false, unique = true)
    private String name;

    @Column(name = "postal_code")
    private String postalCode;

    private String region;

    private String country;

    private String description;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    @JsonBackReference(value = "place-city")
    private List<Place> places;
}
