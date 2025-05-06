package com.projetfilrougeapi.apifilrouge.endpoint_api.invitation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Transactional
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    @SequenceGenerator(
            name = "invitation_id_seq",
            sequenceName = "invitation_id_seq",
            allocationSize = 1
    )
    private Long invitationId;
    private String description;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Status status;


    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true, referencedColumnName = "event_id")
    @JsonBackReference(value = "invitation-events")
    private Event event;

    //TO DO : ajouter une relation avec l'utilisateur

}
