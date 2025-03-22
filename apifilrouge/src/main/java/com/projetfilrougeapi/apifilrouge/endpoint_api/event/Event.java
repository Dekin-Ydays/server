package com.projetfilrougeapi.apifilrouge.endpoint_api.event;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue
    private Integer id;
    private LocalDateTime event_date;
    private String description;
    private String event_name;
    private String address;
    private Integer max_customers;
    private Boolean is_trending;
    private Boolean active;

 /*   @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;*/


}