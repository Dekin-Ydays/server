package com.projetfilrougeapi.apifilrouge.config;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InitDB {

    @Bean
    CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                List<Category> categories = List.of(
                        Category.builder()
                                .name("Sport")
                                .description("Événements sportifs et activités physiques")
                                .key("sport")
                                .isTrending(false)
                                .build(),
                        Category.builder()
                                .name("Culture")
                                .description("Art, musique, théâtre et expositions")
                                .key("culture")
                                .isTrending(true)
                                .build(),
                        Category.builder()
                                .name("Technologie")
                                .description("Conférences tech, meetups et hackathons")
                                .key("technology")
                                .isTrending(false)
                                .build(),
                        Category.builder()
                                .name("Nourriture")
                                .description("Événements culinaires et dégustations")
                                .key("food")
                                .isTrending(false)
                                .build(),
                        Category.builder()
                                .name("Bien-être")
                                .description("Yoga, méditation et activités de bien-être")
                                .key("wellness")
                                .isTrending(false)
                                .build()
                );
                categoryRepository.saveAll(categories);
            }
        };
    }
}
