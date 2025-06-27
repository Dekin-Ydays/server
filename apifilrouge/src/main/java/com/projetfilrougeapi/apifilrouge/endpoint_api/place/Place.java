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
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    private String description;

    private String address;

    private String type;
    private Double latitude;
    private Double longitude;

    @Column(name= "banner_url")
    private String bannerUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "city_name")
    private String cityName;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "place-events")
    @JsonIgnore
    private List<Event> events;

    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    @JsonIgnore
    private City city;

}
