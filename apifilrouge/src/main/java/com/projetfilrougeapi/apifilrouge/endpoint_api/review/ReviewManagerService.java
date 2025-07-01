package com.projetfilrougeapi.apifilrouge.endpoint_api.review;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewManagerService {
    private final UserRepository userRepository;

    public ReviewManagerService( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void manageReviewNote(Review review) {
        User reviewedUser = review.getReviewedUser();

        reviewedUser.setTotalReviews(reviewedUser.getTotalReviews() + 1);
        reviewedUser.setSumTotalNoteReviews(reviewedUser.getSumTotalNoteReviews() + review.getRating());
        reviewedUser.setNote(reviewedUser.getSumTotalNoteReviews() / reviewedUser.getTotalReviews());

        userRepository.save(reviewedUser);
    }

}
