package com.projetfilrougeapi.apifilrouge.app_api.invitation;

import com.projetfilrougeapi.apifilrouge.app_api.event.Event;
import com.projetfilrougeapi.apifilrouge.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_invitation")
public class Invitation {
    @Id
    @GeneratedValue
    private Integer id;
    private String description;
    //private String phone;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}

