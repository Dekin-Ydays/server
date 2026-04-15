package com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike;

import com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class CommentaryLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="commentary_like_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name="commentary_id", nullable = false)
    private Commentary commentary;

    @OneToMany(mappedBy = "commentaryLike", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> individualLikes = new ArrayList<>();

    @Column(name = "commentary_like_sum", nullable = false)
    private Integer commentaryLikeSum = 0;
}
