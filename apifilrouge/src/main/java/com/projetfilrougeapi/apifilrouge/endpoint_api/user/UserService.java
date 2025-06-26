package com.projetfilrougeapi.apifilrouge.endpoint_api.user;

import com.projetfilrougeapi.apifilrouge.DTO.EventSummaryResponse;
import com.projetfilrougeapi.apifilrouge.DTO.UserRequest;
import com.projetfilrougeapi.apifilrouge.DTO.UserResponse;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.CategoryRepository;
import com.projetfilrougeapi.apifilrouge.endpoint_api.event.EventController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.Invitation;
import com.projetfilrougeapi.apifilrouge.endpoint_api.invitation.InvitationController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.report.Report;
import com.projetfilrougeapi.apifilrouge.endpoint_api.report.ReportController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * <p>This method fetches the user's email from the security context, looks up the corresponding
     * {@link User} entity in the database, maps it to a {@link UserResponse}, and wraps it in an
     * {@link EntityModel} with a self-referencing HATEOAS link.</p>
     *
     * @return an {@link EntityModel} containing the current user's profile
     * @throws ResponseStatusException if the user is not found in the database
     */
    public EntityModel<UserResponse> getCurrentUserProfile() {
        String userEmail = getCurrentUserEmail();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : " + userEmail));

        UserResponse response = UserResponse.fromEntity(user);

        return EntityModel.of(response,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
    }

    /**
     * Updates the profile of the currently authenticated user with the provided data.
     *
     * @param request the fields to update (only non-null fields will be modified)
     * @return an {@link EntityModel} containing the updated user profile
     */
    public EntityModel<UserResponse> updateCurrentUserProfile(UserRequest request) {
        try {
            String email = getCurrentUserEmail();

            User existingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));

            if (request.getFirstName() != null) existingUser.setFirstName(request.getFirstName());
            if (request.getLastName() != null) existingUser.setLastName(request.getLastName());
            if (request.getPseudo() != null) existingUser.setPseudo(request.getPseudo());
            if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
            if (request.getPassword() != null) existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            if (request.getPhone() != null) existingUser.setPhone(request.getPhone());
            if (request.getDescription() != null) existingUser.setDescription(request.getDescription());
            if (request.getImageUrl() != null) existingUser.setImageUrl(request.getImageUrl());
            if (request.getBannerUrl() != null) existingUser.setBannerUrl(request.getBannerUrl());
            if (request.getNote() != null) existingUser.setNote(request.getNote());
            if (request.getSocials() != null) existingUser.setSocials(request.getSocials());

            if (request.getCategoryKeys() != null) {
                List<Category> categories = categoryRepository.findByKeyIn(request.getCategoryKeys());

                if (categories.size() != request.getCategoryKeys().size()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more category keys are invalid.");
                }

                existingUser.setCategories(categories);
            }

            User updatedUser = userRepository.save(existingUser);
            UserResponse response = UserResponse.fromEntity(updatedUser);

            return EntityModel.of(response,
                    linkTo(methodOn(UserController.class).getCurrentUserProfile()).withSelfRel());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while updating profile", e);
        }
    }

    /**
     * Set the current authenticated user to dosabled.
     * Throws an exception if the user is not found.
     */
//    public void disableCurrentUser() {
//
//    }

    public EntityModel<UserResponse> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserResponse response = UserResponse.fromEntity(user);
        return EntityModel.of(response,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).getEventsForUser(user.getId())).withRel("events"),
                linkTo(methodOn(UserController.class).getCategoriesForUser(id)).withRel("categories"),
                linkTo(methodOn(UserController.class).getInvitationsForUser(id)).withRel("invitations"));

    }

    public CollectionModel<EntityModel<UserResponse>> getAllUsers() {

        List<EntityModel<UserResponse>> users = userRepository.findAll().stream()
                .map(user -> {
                    UserResponse response = UserResponse.fromEntity(user);
                    return EntityModel.of(response,
                            linkTo(methodOn(EventController.class).getEventById(user.getId())).withSelfRel());
                })
                .collect(Collectors.toList());
        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("places"),
                linkTo(methodOn(EventController.class).getAllEvents(null,null,null,null, null, null, null, null)).withRel("events"),
                linkTo(methodOn(InvitationController.class).getAllInvitations()).withRel("Invitations"),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("Categories"));
    }

    public CollectionModel<EntityModel<EventSummaryResponse>> getEventsForUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        List<EntityModel<EventSummaryResponse>> events = user.getEvents().stream()
                .map(event -> {
                    EventSummaryResponse response = EventSummaryResponse.fromEntity(event);
                    return EntityModel.of(response,
                            linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel(),
                            linkTo(methodOn(UserController.class).getUserById(user.getId())).withRel("user")
                    );
                })
                .collect(Collectors.toList());

        return CollectionModel.of(events,
                linkTo(methodOn(UserController.class).getEventsForUser(id)).withSelfRel());
    }

    public CollectionModel<EntityModel<Invitation>> getInvitationsForUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Invitation>> invitations = user.getInvitations().stream()
                .map(inv -> EntityModel.of(inv,
                        linkTo(methodOn(InvitationController.class).getInvitationById(inv.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUserById(id)).withRel("user")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(invitations,
                linkTo(methodOn(UserController.class).getInvitationsForUser(id)).withSelfRel());
    }

    public CollectionModel<EntityModel<Category>> getCategoriesForUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Category>> categories = user.getCategories().stream()
                .map(cat -> EntityModel.of(cat,
                        linkTo(methodOn(CategoryController.class).getCategoryById(cat.getId())).withSelfRel()
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(categories,
                linkTo(methodOn(UserController.class).getCategoriesForUser(id)).withSelfRel());
    }

    public EntityModel<UserResponse> updateUser(Long id, UserRequest request) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        if (request.getFirstName() != null) existingUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null) existingUser.setLastName(request.getLastName());
        if (request.getPseudo() != null) existingUser.setPseudo(request.getPseudo());
        if (request.getEmail() != null) existingUser.setEmail(request.getEmail());

        if (request.getPassword() != null) existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getPhone() != null) existingUser.setPhone(request.getPhone());
        if (request.getDescription() != null) existingUser.setDescription(request.getDescription());
        if (request.getImageUrl() != null) existingUser.setImageUrl(request.getImageUrl());
        if (request.getBannerUrl() != null) existingUser.setBannerUrl(request.getBannerUrl());
        if (request.getNote() != null) existingUser.setNote(request.getNote());
        if (request.getSocials() != null) existingUser.setSocials(request.getSocials());
        if (request.getRole() != null) existingUser.setRole(request.getRole());

        if (request.getCategoryKeys() != null) {
            List<Category> categories = categoryRepository.findByKeyIn(request.getCategoryKeys());

            if (categories.size() != request.getCategoryKeys().size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or multiples keys are invalides.");
            }
            existingUser.setCategories(categories);
        }

        User updatedUser = userRepository.save(existingUser);
        UserResponse response = UserResponse.fromEntity(updatedUser);

        return EntityModel.of(response,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
    }

    public CollectionModel<EntityModel<EventSummaryResponse>> getParticipatingEvents(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<EntityModel<EventSummaryResponse>> events = user.getParticipatedEvents().stream()
                .map(event -> {
                    EventSummaryResponse response = EventSummaryResponse.fromEntity(event);
                    return EntityModel.of(response,
                            linkTo(methodOn(EventController.class).getEventById(event.getId())).withSelfRel());
                })
                .collect(Collectors.toList());

        return CollectionModel.of(events,
                linkTo(methodOn(UserController.class).getParticipatingEvents(id)).withSelfRel());
    }

    public CollectionModel<EntityModel<Report>> getReportsSentByUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Report>> reports = user.getReportsSent().stream()
                .map(report -> EntityModel.of(report,
                        linkTo(methodOn(ReportController.class).getReportById(report.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withRel("user")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(reports,
                linkTo(methodOn(UserController.class).getReportsSentByUser(id)).withSelfRel());
    }

    public CollectionModel<EntityModel<Report>> getReportsReceivedByUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EntityModel<Report>> reports = user.getReportsReceived().stream()
                .map(report -> EntityModel.of(report,
                        linkTo(methodOn(ReportController.class).getReportById(report.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withRel("user")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(reports,
                linkTo(methodOn(UserController.class).getReportsReceivedByUser(id)).withSelfRel());
    }

    /**
     * Retrieves the email of the currently authenticated user from the security context.
     *
     * @return the email address of the logged-in user
     */
    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

}
