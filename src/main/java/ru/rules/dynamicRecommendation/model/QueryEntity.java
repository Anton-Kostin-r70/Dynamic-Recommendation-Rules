package ru.rules.dynamicRecommendation.model;

import jakarta.persistence.*;

import java.util.UUID;
import java.util.List;

/**
 * Entity representing a query in the dynamic recommendation system.
 * Each query is associated with a rule (RuleEntity) and contains the logic for checking conditions
 * to generate personalized recommendations for users.
 * <p>
 * Corresponds to the "query" table in the database.
 */
@Entity
@Table(name = "query")
public class QueryEntity {

    /**
     * Unique identifier of the query.
     * Automatically generated using UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Type of the query that defines its functional purpose.
     * Stored as a string, corresponds to the values of the QueryType enum.
     * Examples: "USER_OF", "ACTIVE_USER_OF", "TRANSACTION_SUM_COMPARE".
     * <p>
     * This field is required (nullable = false).
     */
    @Column(name = "query_type", nullable = false)
    private String queryType;

    /**
     * Flag indicating whether to negate the query execution result.
     * If true, the query result will be inverted:
     * true → false, false → true.
     * Enables flexible rule logic configuration.
     * <p>
     * This field is required (nullable = false).
     */
    @Column(name = "negate", nullable = false)
    private boolean negate;

    /**
     * List of query arguments containing parameters for its execution.
     * The structure and number of arguments depend on the query type (queryType).
     * For example, for the "USER_OF" type, an argument may contain a product type
     * (DEBIT, CREDIT, etc.).
     * <p>
     * Stored in a separate "query_argument" table with a relationship via the query_id field.
     * Each list element is a string representation of an argument.
     */
    @ElementCollection
    @CollectionTable(name = "query_argument", joinColumns = @JoinColumn(name = "query_id"))
    @Column(name = "argument")
    private List<String> arguments;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
