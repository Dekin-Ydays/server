package com.projetfilrougeapi.apifilrouge.endpoint_api.user;

import com.fasterxml.jackson.annotation.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.report.Report;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String pseudo;

    private String phone;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    private Double note;

    @Column(columnDefinition = "TEXT")
    private String socials;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private int countReportsReceived=0;

    private boolean isBanned = false;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-events")
    private List<Event> events;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-invitations")
    private List<Invitation> invitations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-orders")
    private List<Order> orders;

    @ManyToMany
    @JoinTable(
            name = "user_category",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnoreProperties("users")
    private List<Category> categories = new ArrayList<>();

    @ManyToMany(mappedBy = "participants")
    @JsonIgnoreProperties("participants")
    private List<Event> participatedEvents = new ArrayList<>();

    @OneToMany(mappedBy = "senderUser", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-reports-sent")
    private List<Report> reportsSent;

    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-reports-received")
    private List<Report> reportsReceived;

    // === Spring Security methods ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();  // Utilise la méthode getAuthorities() de l'enum Role
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}