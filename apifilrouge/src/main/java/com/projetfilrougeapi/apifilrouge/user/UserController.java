package com.projetfilrougeapi.apifilrouge.user;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
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

    @PatchMapping
    public EntityModel<User> patchUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    // Add your endpoint methods here, for example:
    // @GetMapping("/{id}")
    // public ResponseEntity<User> getUserById(@PathVariable Long id) {
    //     return ResponseEntity.ok(userService.getUserById(id));
    // }
}
