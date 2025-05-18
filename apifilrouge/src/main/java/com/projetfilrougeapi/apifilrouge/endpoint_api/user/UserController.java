package com.projetfilrougeapi.apifilrouge.endpoint_api.user;

import com.projetfilrougeapi.apifilrouge.DTO.UserRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public CollectionModel<EntityModel<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public EntityModel<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/events")
    public CollectionModel<EntityModel<Event>> getEventsForUser(@PathVariable Long id) {
        return userService.getEventsForUser(id);
    }
    @GetMapping("/{id}/invitations")
    public CollectionModel<EntityModel<Invitation>> getInvitationsForUser(@PathVariable Long id) {
        return userService.getInvitationsForUser(id);
    }

    @GetMapping("/{id}/categories")
    public CollectionModel<EntityModel<Category>> getCategoriesForUser(@PathVariable Long id) {
        return userService.getCategoriesForUser(id);
    }

    @PatchMapping("/{id}")
    public EntityModel<User> patchUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }
}