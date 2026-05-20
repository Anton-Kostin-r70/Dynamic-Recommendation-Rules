package ru.rules.dynamicRecommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rules.dynamicRecommendation.model.Users;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities in the dynamic recommendation system.
 * Provides CRUD operations and custom query methods for persistence and retrieval of user data.
 * Extends JpaRepository to leverage Spring Data JPA functionality for database operations.
 * The repository automatically scans the package and subpackages for entity classes
 * and generates implementation for declared methods based on naming conventions.
 */
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     * Checks whether a user with the specified ID exists in the database.
     *
     * @param id the unique identifier of the user to check; must not be null
     * @return true if a user with the given ID exists, false otherwise
     * @throws IllegalArgumentException if the id is null
     */
    boolean existsById(Long id);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve; must not be null
     * @return Optional containing the User if found, empty Optional otherwise
     * @throws IllegalArgumentException if the id is null
     */
    Optional<Users> findById(Long id);

    /**
     * Finds a user by their name (case‑insensitive search).
     *
     * @param name the name of the user to search for; must not be null or empty
     * @return Optional containing the User if a matching record is found, empty Optional otherwise
     * @throws IllegalArgumentException if the name is null or blank
     */
    Optional<Users> findByNameIgnoreCase(String name);

    /**
     * Retrieves all users whose names contain the specified substring (case‑insensitive).
     * Useful for search functionality and user lookup.
     *
     * @param nameSubstring the substring to search for within user names; must not be null or empty
     * @return list of matching User entities; never null (empty list if no matches)
     * @throws IllegalArgumentException if the substring is null or blank
     */
    List<Users> findByNameContainingIgnoreCase(String nameSubstring);

    /**
     * Counts the total number of users in the system.
     *
     * @return the number of user records in the database
     */
    long count();

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the unique identifier of the user to delete; must not be null
     * @throws IllegalArgumentException if the id is null
     */
    void deleteById(Long id);
}