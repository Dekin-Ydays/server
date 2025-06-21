package com.projetfilrougeapi.apifilrouge.endpoint_api.review;

import com.projetfilrougeapi.apifilrouge.DTO.ReviewRequest;
import com.projetfilrougeapi.apifilrouge.DTO.UserSummary;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public EntityModel<Review> createReview(ReviewRequest requestReview) {

        User sender = userRepository.findById(requestReview.getSenderUserId())
                .orElseThrow(() -> new RuntimeException("Sender user not found with id: " + requestReview.getSenderUserId()));
        User reportedUser = userRepository.findById(requestReview.getReviewedUserId())
                .orElseThrow(() -> new RuntimeException("Reported user not found with id: " + requestReview.getReviewedUserId()));

     //   Review review = New Review()
        Review review = new Review(requestReview.getContent(), requestReview.getRating(), sender, reportedUser);



        Review savedReview = reviewRepository.save(review);

        return EntityModel.of(savedReview,
                linkTo(methodOn(ReviewController.class).getReviewById(savedReview.getReview_id())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).getAllReview()).withRel("reviews"),
                linkTo(methodOn(UserController.class).getUserById(review.getSenderReviewUser().getId())).withRel("senderUser"),
                linkTo(methodOn(UserController.class).getUserById(review.getReviewedUser().getId())).withRel("reviewedUser")
        );
    }

    public CollectionModel<EntityModel<Review>> getAllReview() {
        var reviews = reviewRepository.findAll().stream()
                .map(review -> EntityModel.of(review,
                        linkTo(methodOn(ReviewController.class).getReviewById(review.getReview_id())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUserById(review.getSenderReviewUser().getId())).withRel("senderUser"),
                        linkTo(methodOn(UserController.class).getUserById(review.getReviewedUser().getId())).withRel("reviewedUser")))
                .toList();

        return CollectionModel.of(reviews,
                linkTo(methodOn(ReviewController.class).getAllReview()).withSelfRel(),
                linkTo(methodOn(ReviewController.class).getAllReview()).withRel("reviews"));
    }

    public EntityModel<Review> getReviewById(Long id)
{
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        return EntityModel.of(review,
                linkTo(methodOn(ReviewController.class).getReviewById(review.getReview_id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUserById(review.getSenderReviewUser().getId())).withRel("senderUser"),
                linkTo(methodOn(UserController.class).getUserById(review.getReviewedUser().getId())).withRel("reviewedUser"));
    }

    public EntityModel<UserSummary> getSenderUserByReviewId(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        User senderUser = review.getSenderReviewUser();
        UserSummary userSummary = new UserSummary(senderUser.getId(), senderUser.getUsername(), senderUser.getEmail());

        return EntityModel.of(userSummary,
                linkTo(methodOn(ReviewController.class).getSenderUserByReviewId(review.getReview_id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUserById(senderUser.getId())).withRel("user"));
    }

    public EntityModel<UserSummary> getReviewedUserByReviewId(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        User reviewedUser = review.getReviewedUser();
        UserSummary userSummary = new UserSummary(reviewedUser.getId(), reviewedUser.getUsername(), reviewedUser.getEmail());

        return EntityModel.of(userSummary,
                linkTo(methodOn(ReviewController.class).getReviewedUserByReviewId(review.getReview_id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUserById(reviewedUser.getId())).withRel("user"));
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        reviewRepository.delete(review);
    }
}
