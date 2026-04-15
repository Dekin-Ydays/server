package com.projetfilrougeapi.apifilrouge.endpoint_api.commentary;

import com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike.CommentaryLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Commentary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="commentary_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "social_post_id", nullable = false)
    private SocialPost socialPost;

    @OneToOne(mappedBy = "commentary", cascade = CascadeType.ALL, orphanRemoval = true)
    private CommentaryLike commentaryLike;

    public Commentary() {
        this.commentaryLike = new CommentaryLike();
        this.commentaryLike.setCommentary(this);
        this.commentaryLike.setCommentaryLikeSum(0);
    }
}
