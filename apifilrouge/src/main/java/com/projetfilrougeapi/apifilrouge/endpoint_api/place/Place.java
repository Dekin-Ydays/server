package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "place_id")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long place_id;
    private String city_name;

    @OneToMany(mappedBy = "place")// One place can have many events
    private List<Event> events;



}
