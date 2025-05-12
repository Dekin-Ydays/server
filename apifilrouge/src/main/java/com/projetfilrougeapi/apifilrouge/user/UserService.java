package com.projetfilrougeapi.apifilrouge.user;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.InvitationController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.place.PlaceController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public EntityModel<User> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).getEventsForUser(user.getId())).withRel("events"),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("Invitations"),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("Categories"));
    }


    public CollectionModel<EntityModel<User>> getAllUsers() {
        List<EntityModel<User>> users = userRepository.findAll().stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("places"),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("Invitations"),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("Categories"));
    }

     public CollectionModel<EntityModel<Event>> getEventsForUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Event>> events = user.getEvents().stream()
                .map(event -> EntityModel.of(event,
                        linkTo(methodOn(EventController.class).getEventById(event.getEvent_id())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withRel("user")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(events,
                linkTo(methodOn(UserController.class).getEventsForUser(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("Invitations"),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("Categories"));

     }
    // à modifier pour le DTO
    public EntityModel<User> updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (existingUser.getFirstName() != null ) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (existingUser.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        //existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());
        //existingUser.setPhone(user.getPhone());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());
        existingUser.setEvents(user.getEvents());

        userRepository.save(existingUser);

        return EntityModel.of(existingUser,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel("events"),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("Invitations"),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("Categories"));
    }
}
