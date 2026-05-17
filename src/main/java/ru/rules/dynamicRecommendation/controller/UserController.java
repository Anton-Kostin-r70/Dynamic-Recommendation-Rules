package ru.rules.dynamicRecommendation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rules.dynamicRecommendation.model.User;
import ru.rules.dynamicRecommendation.service.UserService;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for user management endpoints.
 * Handles HTTP requests and delegates business logic to UserService.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET endpoint to retrieve a user by ID.
     *
     * @param id the user ID
     * @return ResponseEntity with user data or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST endpoint to create a new user.
     *
     * @param user the user data from request body
     * @return ResponseEntity with created user and 201 status
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(201).body(createdUser);
    }

    /**
     * PUT endpoint to update an existing user.
     *
     * @param id   the user ID to update
     * @param user updated user data
     * @return ResponseEntity with updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * DELETE endpoint to delete a user by ID.
     *
     * @param id the user ID to delete
     * @return ResponseEntity with 204 status (no content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * GET endpoint to retrieve all users.
     *
     * @return ResponseEntity with list of all users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
