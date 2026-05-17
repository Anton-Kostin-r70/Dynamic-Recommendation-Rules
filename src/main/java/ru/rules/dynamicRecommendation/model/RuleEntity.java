package ru.rules.dynamicRecommendation.model;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a recommendation rule in the dynamic recommendation system.
 * Each rule is associated with a financial product and contains a set of queries
 * that define the conditions for recommending this product to users.
 * Corresponds to the "rule" table in the database.
 */
@Entity
@Table(name = "rule")
public class RuleEntity {

    /**
     * Unique identifier of the rule.
     * Automatically generated using UUID when a new rule is persisted.
     */
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Identifier of the financial product associated with this rule.
     * Used to link the rule to a specific product in the system.
     * Corresponds to the "product_id" column in the database (cannot be null).
     */
    @Setter
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * Name of the financial product associated with this rule.
     * Human‑readable representation of the product (e.g., "Premium Savings Account").
     * Corresponds to the "product_name" column in the database (cannot be null).
     */
    @Column(name = "product_name", nullable = false)
    private String productName;

    /**
     * Detailed description or promotional text for the product.
     * May contain marketing copy, terms, or other information shown to users.
     * Stored as TEXT in the database ("product_text" column, cannot be null).
     * The columnDefinition specifies the SQL type as TEXT for large text storage.
     */
    @Column(name = "product_text", nullable = false, columnDefinition = "TEXT")
    private String productText;

    /**
     * Timestamp indicating when the rule was created.
     * Records the exact date and time of rule creation.
     * Corresponds to the "created_at" column in the database (cannot be null).
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * List of queries that define the conditions for applying this rule.
     * Each query checks a specific condition (e.g., user activity, transaction sums).
     * All queries must evaluate to true for the rule to trigger a recommendation.
     * Mapped as a one‑to‑many relationship with QueryEntity:
     * - CascadeType.ALL: all operations (persist, merge, remove) cascade to queries.
     * - FetchType.LAZY: queries are loaded on demand (improves performance).
     * - orphanRemoval = true: removes orphaned queries when removed from the list.
     * Joined via the "rule_id" foreign key column in the query table.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "rule_id")
    private List<QueryEntity> queries;

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<QueryEntity> getQueries() {
        return queries;
    }

    public void setQueries(List<QueryEntity> queries) {
        this.queries = queries;
    }
}
