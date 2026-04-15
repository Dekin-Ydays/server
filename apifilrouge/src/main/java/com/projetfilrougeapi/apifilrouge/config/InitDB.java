package com.projetfilrougeapi.apifilrouge.config;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.CommentaryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.LikeRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.commentaryLike.CommentaryLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.like.socialPostLike.SocialPostLike;
import com.projetfilrougeapi.apifilrouge.endpoint_api.subscription.SubscriptionRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.AuthProvider;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.video.VideoRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Configuration pour l'initialisation de la base de données.
 * <p>
 * Cette classe est responsable de l'initialisation des données par défaut dans la base de données
 * au démarrage de l'application. Elle définit des beans CommandLineRunner qui s'exécutent
 * lors du lancement de l'application pour peupler les tables avec des données initiales.
 * </p>
 */
@Configuration
public class InitDB {

    /**
     * Initialise des utilisateurs par défaut dans la base de données si aucun n'existe déjà.
     * <p>
     * Ce bean crée quatre utilisateurs avec différents rôles (User, Admin, AuthService, Organizer)
     * et les enregistre dans la base de données uniquement si celle-ci est vide.
     * Les mots de passe sont encodés avant d'être stockés.
     * </p>
     *
     * @param userRepository  Le repository pour accéder aux données des utilisateurs
     * @param passwordEncoder L'encodeur utilisé pour sécuriser les mots de passe
     * @return Un CommandLineRunner qui initialise les utilisateurs
     */
    @Bean
    CommandLineRunner initUsers(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                CategoryRepository categoryRepository,
                                VideoRepository videoRepository,
                                SocialPostRepository socialPostRepository,
                                CommentaryRepository commentaryRepository,
                                LikeRepository likeRepository,
                                SubscriptionRepository subscriptionRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                List<com.projetfilrougeapi.apifilrouge.endpoint_api.user.User> users = List.of(
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("User")
                                .lastName("Normal")
                                .email("user@example.com")
                                .pseudo("user")
                                .slug("user")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("Password@123"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.User)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Admin")
                                .lastName("System")
                                .email("admin@example.com")
                                .pseudo("admin")
                                .slug("admin")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("Password@123"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.Admin)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.user.User.builder()
                                .firstName("Auth")
                                .lastName("Service")
                                .email("auth@example.com")
                                .pseudo("auth")
                                .slug("auth")
                                .provider(AuthProvider.LOCAL)
                                .password(passwordEncoder.encode("Password@123"))
                                .role(com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role.AuthService)
                                .build()
                );
                userRepository.saveAll(users);
            }

            if (categoryRepository.count() == 0) {
                List<com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category> categories = List.of(
                        com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category.builder()
                                .categoryName("Rock")
                                .categoryDescription("Rock music category")
                                .typeCategory(com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory.Rock)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category.builder()
                                .categoryName("Kpop")
                                .categoryDescription("Kpop music category")
                                .typeCategory(com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory.Kpop)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category.builder()
                                .categoryName("Jazz")
                                .categoryDescription("Jazz music category")
                                .typeCategory(com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory.Jazz)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category.builder()
                                .categoryName("HipHop")
                                .categoryDescription("HipHop music category")
                                .typeCategory(com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory.HipHop)
                                .build()
                );
                categoryRepository.saveAll(categories);
            }

            if (videoRepository.count() == 0) {
                List<com.projetfilrougeapi.apifilrouge.endpoint_api.video.Video> videos = List.of(
                        com.projetfilrougeapi.apifilrouge.endpoint_api.video.Video.builder()
                                .url("http://example.com/video1")
                                .videoScore(10)
                                .build(),
                        com.projetfilrougeapi.apifilrouge.endpoint_api.video.Video.builder()
                                .url("http://example.com/video2")
                                .videoScore(19)
                                .build()
                );
                videoRepository.saveAll(videos);
            }

            if (socialPostRepository.count() == 0) {
                var user = userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé !"));

                var video = videoRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Aucune vidéo trouvée !"));

                var category = categoryRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Aucune catégorie trouvée !"));

                com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost post1 =
                        com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost.builder()
                                .socialPostDescription("Ceci est mon premier post de test automatique !")
                                .user(user)
                                .video(video)
                                .category(category)
                                .build();

                com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost post2 =
                        com.projetfilrougeapi.apifilrouge.endpoint_api.socialPost.SocialPost.builder()
                                .socialPostDescription("Un deuxième post pour tester la pagination")
                                .user(user)
                                .video(video)
                                .category(category)
                                .build();

                SocialPostLike counter = new SocialPostLike();
                counter.setSocialPost(post1);
                counter.setSocialPostLikeSum(1);
                post1.setSocialPostLike(counter);

                SocialPostLike counter2 = new SocialPostLike();
                counter2.setSocialPost(post2);
                counter2.setSocialPostLikeSum(1);
                post2.setSocialPostLike(counter2);

                com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like firstLike =
                        com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like.builder()
                                .user(user)
                                .socialPost(post1)
                                .socialPostLike(counter)
                                .build();

                com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like secondLike =
                        com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like.builder()
                                .user(user)
                                .socialPost(post2)
                                .socialPostLike(counter2)
                                .build();

                socialPostRepository.saveAll(List.of(post1, post2));
                likeRepository.saveAll(List.of(firstLike, secondLike));
            }

            if (commentaryRepository.count() == 0) {
                var user = userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé !"));

                var post = socialPostRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Aucun post trouvé !"));

                com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary commentary1 =
                        com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary.builder()
                                .content("Commentaire de test sur le premier post !")
                                .user(user)
                                .socialPost(post)
                                .build();

                com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary commentary2 =
                        com.projetfilrougeapi.apifilrouge.endpoint_api.commentary.Commentary.builder()
                                .content("Un deuxième commentaire pour tester la pagination !")
                                .user(user)
                                .socialPost(post)
                                .build();

                CommentaryLike counter = new CommentaryLike();
                counter.setCommentary(commentary1);
                counter.setCommentaryLikeSum(1);
                commentary1.setCommentaryLike(counter);

                CommentaryLike counter2 = new CommentaryLike();
                counter2.setCommentary(commentary2);
                counter2.setCommentaryLikeSum(1);
                commentary2.setCommentaryLike(counter2);

                com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like firstLike =
                        com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like.builder()
                                .user(user)
                                .commentary(commentary1)
                                .commentaryLike(counter)
                                .build();

                com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like secondLike =
                        com.projetfilrougeapi.apifilrouge.endpoint_api.like.Like.builder()
                                .user(user)
                                .commentary(commentary2)
                                .commentaryLike(counter2)
                                .build();

                commentaryRepository.saveAll(List.of(commentary1, commentary2));
                likeRepository.saveAll(List.of(firstLike, secondLike));
            }

            if (subscriptionRepository.count() == 0) {
                List<com.projetfilrougeapi.apifilrouge.endpoint_api.user.User> allUsers = userRepository.findAll();

                if (allUsers.size() >= 2) {
                    var subscriber = allUsers.get(0);
                    var subscribed = allUsers.get(1);

                    com.projetfilrougeapi.apifilrouge.endpoint_api.subscription.Subscription sub1 =
                            com.projetfilrougeapi.apifilrouge.endpoint_api.subscription.Subscription.builder()
                                    .subscriber(subscriber)
                                    .subscribed(subscribed)
                                    .build();

                    com.projetfilrougeapi.apifilrouge.endpoint_api.subscription.Subscription sub2 =
                            com.projetfilrougeapi.apifilrouge.endpoint_api.subscription.Subscription.builder()
                                    .subscriber(subscribed)
                                    .subscribed(subscriber)
                                    .build();

                    subscriptionRepository.saveAll(List.of(sub1, sub2));
                }
            }
        };
    }
}
