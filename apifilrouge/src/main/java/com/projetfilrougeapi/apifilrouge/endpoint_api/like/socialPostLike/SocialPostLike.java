package com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike;

import com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost;
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
public class SocialPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="social_post_like_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name="social_post_id", nullable = false)
    private SocialPost socialPost;

    @OneToMany(mappedBy = "socialPostLike", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> individualLikes = new ArrayList<>();

    @Column(name = "social_post_like_sum", nullable = false)
    private Integer socialPostLikeSum = 0;
}
