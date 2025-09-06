package com.projetfilrougeapi.apifilrouge.endpoint_api.review;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewManagerService {
    private final UserRepository userRepository;

    public ReviewManagerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Met à jour la note moyenne d'un utilisateur après qu'il ait reçu un nouvel avis.
     * <p>
     * Cette méthode :
     * 1. Incrémente le compteur total d'avis reçus
     * 2. Ajoute la note du nouvel avis à la somme totale (gère le cas null pour le premier avis)
     * 3. Recalcule la note moyenne (somme totale / nombre d'avis)
     * 4. Sauvegarde les modifications en base de données
     *
     * @param review L'avis qui vient d'être créé
     */
    public void manageReviewNote(Review review) {
        User reviewedUser = review.getReviewedUser();

        reviewedUser.setTotalReviews(reviewedUser.getTotalReviews() + 1);

        Double currentSum = reviewedUser.getSumTotalNoteReviews();
        if (currentSum == null) {
            currentSum = 0.0;  // Si c'est null, on met 0
        }

        Double newSum = currentSum + review.getRating();
        reviewedUser.setSumTotalNoteReviews(newSum);
        reviewedUser.setNote(newSum / reviewedUser.getTotalReviews());

        userRepository.save(reviewedUser);
    }
}