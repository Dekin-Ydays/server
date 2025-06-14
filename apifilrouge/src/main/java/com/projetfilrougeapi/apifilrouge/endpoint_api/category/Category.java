package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    @Column(name = "is_trending")
    private boolean isTrending;

    private String key;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnoreProperties("categories-user")
    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "categories")
    @JsonIgnoreProperties("categories-events")
    private List<Event> events = new ArrayList<>();
}