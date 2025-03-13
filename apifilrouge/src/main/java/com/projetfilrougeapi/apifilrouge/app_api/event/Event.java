package com.projetfilrougeapi.apifilrouge.app_api.event;

import com.projetfilrougeapi.apifilrouge.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_event")

public class Event {
    @Id
    @GeneratedValue
    private Integer id;
    private LocalDateTime event_date;
    private String description;
    private String event_name;
    private String address;
    private Integer max_customers;
    private Boolean is_trending;
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    public Integer getId() {
        return id;
    }

    public LocalDateTime getEvent_date() {
        return event_date;
    }

    public String getDescription() {
        return description;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getAddress() {
        return address;
    }

    public Integer getMax_customers() {
        return max_customers;
    }

    public Boolean getIs_trending() {
        return is_trending;
    }

    public Boolean getActive() {
        return active;
    }

    public User getCreator() {
        return creator;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEvent_date(LocalDateTime event_date) {
        this.event_date = event_date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMax_customers(Integer max_customers) {
        this.max_customers = max_customers;
    }

    public void setIs_trending(Boolean is_trending) {
        this.is_trending = is_trending;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
    //    @OneToOne
//    @JoinColumn(name = "list_id")
//    private List list;
}
