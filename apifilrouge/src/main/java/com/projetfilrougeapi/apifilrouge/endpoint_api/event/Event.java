package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
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
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "event_id")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long event_id;
    private LocalDateTime event_date;
    private String description;
    private String event_name;
    private String address;
    private Integer max_customers;
    private Boolean is_trending;
    private Boolean active;
    @OneToMany(mappedBy = "event")// One event can have many invitations
    private List<Invitation> invitations;

    @ManyToOne // One event can have one place
    @JoinColumn(name = "place_id", nullable = true)
    private Place place;

    @JsonProperty("place_id") // Map the place ID during deserialization
    public void setPlaceId(Integer placeId) {
        if (placeId == null) {
            this.place = null;
        }else {
            this.place = new Place();
            this.place.setPlace_id(Long.valueOf(placeId));
        }
    }


}