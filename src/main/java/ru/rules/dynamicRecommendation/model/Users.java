package ru.rules.dynamicRecommendation.model;

import jakarta.persistence.*;

/**
 * Entity representing a user in the dynamic recommendation system.
 * Stores basic user information used for recommendation logic and transaction tracking.
 * Corresponds to the "users" table in the database.
 */
@Entity
@Table(name = "users")
public class Users {

    /**
     * Unique identifier of the user.
     * Generated automatically using a database sequence (GenerationType.SEQUENCE).
     * Serves as the primary key for the users table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    /**
     * Name of the user.
     * A required field that stores the user's name (e.g., full name or username).
     * Corresponds to the "name" column in the database (cannot be null).
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Default constructor required by JPA.
     * Should not be used for creating new user instances — use the parameterized constructor instead.
     */
    public Users() {
    }

    /**
     * Constructor to create a new user with specified ID and name.
     *
     * @param id   unique identifier for the user; if null, the database will generate it
     *             via the sequence when persisted
     * @param name the user's name; must not be null (enforced by the @Column constraint)
     * @throws IllegalArgumentException if name is null
     */
    public Users(Long id, String name) {
        if (name == null) {
            throw new IllegalArgumentException("User name cannot be null");
        }
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the name to assign to the user; must not be null
     * @throws IllegalArgumentException if name is null
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("User name cannot be null");
        }
        this.name = name;
    }
}