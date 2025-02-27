package com.projetfilrougeapi.apifilrouge.app_api.event;

import com.projetfilrougeapi.apifilrouge.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_event")

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

//    @OneToOne
//    @JoinColumn(name = "list_id")
//    private Event event;
}
