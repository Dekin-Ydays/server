package com.projetfilrougeapi.apifilrouge.endpoint_api.category;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.user.User;
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
    private Long id;
    private String name;
    private String description;
    private boolean isTrending;

//    @ManyToMany(mappedBy = "categories")
//    @JsonBackReference(value = "user-category")
//    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "categories")
    @JsonIgnoreProperties("categories")
    private List<Event> events = new ArrayList<>();
}