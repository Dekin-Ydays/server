package com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike.SocialPostLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.video.Video;
import com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
public class SocialPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="social_post_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name="social_post_description", columnDefinition = "TEXT")
    private String socialPostDescription;

    @ManyToOne
    @JoinColumn(name="video_id", nullable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name="category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "socialPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentary> commentaries = new ArrayList<>();

    @OneToOne(mappedBy = "socialPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private SocialPostLike socialPostLike;

    public SocialPost() {
        this.socialPostLike = new SocialPostLike();
        this.socialPostLike.setSocialPost(this);
        this.socialPostLike.setSocialPostLikeSum(0);
    }

}
