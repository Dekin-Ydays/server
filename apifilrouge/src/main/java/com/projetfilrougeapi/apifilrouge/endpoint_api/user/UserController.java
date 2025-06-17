package com.projetfilrougeapi.apifilrouge.endpoint_api.user;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.UserRequest;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.report.Report;
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
    public CollectionModel<EntityModel<UserResponse>> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/me")
    public EntityModel<UserResponse> getCurrentUserProfile() {
        return userService.getCurrentUserProfile();
    }
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/events")
    public CollectionModel<EntityModel<EventSummaryResponse>> getEventsForUser(@PathVariable Long id) {
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

    @GetMapping("/{userId}/participating-events")
    public CollectionModel<EntityModel<EventSummaryResponse>> getParticipatingEvents(@PathVariable Long userId) {
        return userService.getParticipatingEvents(userId);
    }
    @PatchMapping("/{id}")
    public EntityModel<UserResponse> patchUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }
    @GetMapping("/{id}/reports-sent")
    public CollectionModel<EntityModel<Report>> getReportsSentByUser(@PathVariable Long id) {
        return userService.getReportsSentByUser(id);
    }

    @GetMapping("/{id}/reports-received")
    public CollectionModel<EntityModel<Report>> getReportsReceivedByUser(@PathVariable Long id) {
        return userService.getReportsReceivedByUser(id);
    }

}