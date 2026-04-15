package com.projetfilrougeapi.apifilrouge.endpoint_api.like;

import com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike.CommentaryLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike.SocialPostLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "social_post_id"}),
        @UniqueConstraint(columnNames = {"user_id", "commentary_id"})
})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="like_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="social_post_id", nullable = true)
    private SocialPost socialPost;

    @ManyToOne
    @JoinColumn(name="commentary_id", nullable = true)
    private Commentary commentary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="social_post_like_id", nullable = true)
    private SocialPostLike socialPostLike;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="commentary_like_id", nullable = true)
    private CommentaryLike commentaryLike;
}
