package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import com.fasterxml.jackson.annotation.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.Place;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
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
@Getter
@Setter
@Transactional
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

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;
}
