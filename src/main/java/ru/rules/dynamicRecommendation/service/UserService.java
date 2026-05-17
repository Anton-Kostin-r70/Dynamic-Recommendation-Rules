package ru.rules.dynamicRecommendation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rules.dynamicRecommendation.model.User;
import ru.rules.dynamicRecommendation.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for user management operations.
 * Contains business logic for user CRUD operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative");
        }
        return userRepository.findById(id);
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the saved user entity
     * @throws IllegalArgumentException if user is null
     */
    @Transactional
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userRepository.save(user);
    }

    /**
     * Updates an existing user.
     *
     * @param id          the ID of the user to update
     * @param userDetails the updated user details
     * @return the updated user entity
     * @throws IllegalArgumentException if ID is invalid or user not found
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative");
        }
        if (userDetails == null) {
            throw new IllegalArgumentException("User details cannot be null");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        existingUser.setName(userDetails.getName());
        return userRepository.save(existingUser);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @throws IllegalArgumentException if ID is invalid
     */
    @Transactional
    public void deleteUser(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative");
        }
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Retrieves all users.
     *
     * @return list of all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}