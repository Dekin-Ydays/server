package com.projetfilrougeapi.apifilrouge.endpoint_api.user;

import com.projetfilrougeapi.apifilrouge.DTO.*;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.order.Order;
import com.projetfilrougeapi.apifilrouge.endpoint_api.report.Report;
import com.projetfilrougeapi.apifilrouge.endpoint_api.review.Review;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
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

    @GetMapping("/me/received-invitations")
    public CollectionModel<EntityModel<Invitation>> getReceivedInvitations() {
        return userService.getReceivedInvitations();
    }

    @PatchMapping("/me")
    public EntityModel<UserResponse> updateCurrentUser(@Valid @RequestBody UserRequest request) {
        return userService.updateCurrentUserProfile(request);
    }

    @GetMapping("/organizers")
    public PagedModel<EntityModel<OrganizerResponse>> getAllOrganizers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return userService.getAllOrganizers(pageable);
    }

    @GetMapping("/{id}")
    public EntityModel<UserResponse> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/slug/{slug}")
    public EntityModel<UserResponse> findUserBySlug(@PathVariable("slug") String slug) {
        return userService.findUserBySlug(slug);
    }

    @GetMapping("/{id}/events")
    public PagedModel<EntityModel<EventSummaryResponse>> getEventsForUser(@PathVariable("id") Long id,
                                                                               @PageableDefault(size = 10, sort = "id") Pageable pageable)
    {
        return userService.getEventsForUser(id , pageable);
    }

    @GetMapping("/{id}/invitations")
    public CollectionModel<EntityModel<Invitation>> getInvitationsForUser(@PathVariable("id") Long id) {
        return userService.getInvitationsForUser(id);
    }

    @GetMapping("/{id}/categories")
    public CollectionModel<EntityModel<Category>> getCategoriesForUser(@PathVariable("id") Long id) {
        return userService.getCategoriesForUser(id);
    }

    @GetMapping("/{id}/participating-events")
    public CollectionModel<EntityModel<EventSummaryResponse>> getParticipatingEvents(@PathVariable("id") Long id) {
        return userService.getParticipatingEvents(id);
    }

    @PatchMapping("/{id}")
    public EntityModel<UserResponse> patchUser(@Valid @PathVariable("id") Long id, @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @GetMapping("/{id}/reports-sent")
    public CollectionModel<EntityModel<Report>> getReportsSentByUser(@PathVariable("id") Long id) {
        return userService.getReportsSentByUser(id);
    }

    @GetMapping("/{id}/reports-received")
    public CollectionModel<EntityModel<Report>> getReportsReceivedByUser(@PathVariable("id") Long id) {
        return userService.getReportsReceivedByUser(id);
    }

    @GetMapping("/{id}/orders")
    public CollectionModel<EntityModel<OrderResponse>> getOrderByUser(@PathVariable("id") Long id) {
        return userService.getOrderByUser(id);
    }

    @GetMapping("/me/orders")
    public CollectionModel<EntityModel<OrderResponse>> getMyOrders() {
        return userService.getOrdersForCurrentUser();
    }

    @GetMapping("/{id}/received-reviews")
    public CollectionModel<EntityModel<Review>> getReviewByUser(@PathVariable("id") Long id) {
        return userService.getReviewByUser(id);
    }
}