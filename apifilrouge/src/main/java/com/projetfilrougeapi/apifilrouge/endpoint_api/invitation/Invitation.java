package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;

import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.user.User;
import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    //private String phone;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Status status;


   /* @OneToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User sender;*/

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true, referencedColumnName = "event_id")
    private Event event;

    @JsonProperty("event_id") // Map the place ID during deserialization
    public void setPlaceId(Integer event_id) {
        if (event_id == null) {
            this.event = null;
        }else {
            this.event = new Event();
            this.event.setEvent_id(Long.valueOf(event_id));
        }
    }
}

