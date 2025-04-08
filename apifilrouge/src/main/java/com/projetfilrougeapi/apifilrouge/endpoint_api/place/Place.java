package com.projetfilrougeapi.apifilrouge.endpoint_api.place;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Transactional
/**@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "place_id")*/
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    @SequenceGenerator(
            name = "place_id_seq",
            sequenceName = "place_id_seq",
            allocationSize = 1
    )
    private Long placeId;
    private String city_name;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<Event> events;



}
