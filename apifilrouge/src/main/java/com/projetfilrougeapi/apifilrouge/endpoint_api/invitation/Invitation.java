package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;

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
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    //private String phone;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore // Ignore the User object during deserialization
    private User sender;

    @JsonProperty("sender") // Map the sender ID during deserialization
    public void setSenderId(Integer userId) {
        this.sender = new User();
        this.sender.setId(userId);
    }

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false, referencedColumnName = "id")
    private Event event;

    @Version
    private Integer version;
}

