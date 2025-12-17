package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable=false, updatable = false, unique = true)
    private Long id;

    @Column(name="category_name", nullable = false)
    private String categoryName;

    @Column(name="category_description", columnDefinition = "TEXT")
    private String categoryDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TypeCategory typeCategory;
}
