package com.projetfilrougeapi.apifilrouge.endpoint_api.video;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="video_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="video_url", columnDefinition = "TEXT", nullable = false)
    private String url;

    @Column(name="video_score", nullable = false)
    private Integer videoScore;
}
