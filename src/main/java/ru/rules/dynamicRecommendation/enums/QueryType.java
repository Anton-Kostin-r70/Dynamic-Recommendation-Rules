package ru.rules.dynamicRecommendation.enums;

/**
 * Defines the types of queries supported by the system for user and transaction analysis.
 * Used to categorize different data retrieval and comparison operations.
 */
public enum QueryType {
    /**
     * Determines whether the target user (for whom recommendations are being generated)
     * is a user of a specific product.
     * <p>
     * This query verifies the user's association with a given product, which is essential
     * for personalized recommendation systems. If the user is not associated with the product,
     * certain recommendations may be irrelevant.
     * <p>
     * Example: Check if user U123 is a user of product "Premium Savings Account".
     * <p>
     * Parameters:
     * <ul>
     *   <li><b>productType</b> — the identifier of product X (first argument of the query)</li>
     * </ul>
     * <p>
     * Returns:
     * <ul>
     *   <li>{@code true} — if the user has an active relationship with product X</li>
     *   <li>{@code false} — if no association exists or the user is inactive for this product</li>
     * </ul>
     */
    USER_OF("USER_OF"),
    /**
     * Determines whether the target user (for whom recommendations are being generated)
     * is an active user of a specific product.
     * <p>
     * This query verifies both the user's association with a given product and their active status.
     * It's crucial for personalized recommendation systems: inactive users may not be eligible
     * for certain product‑specific offers or features.
     * <p>
     * Example: Check if user U456 is an active user of product "Gold Credit Card".
     * <p>
     * Parameters:
     * <ul>
     *   <li><b>productId</b> — the identifier of product X (first argument of the query)</li>
     * </ul>
     * <p>
     * Returns:
     * <ul>
     *   <li>{@code true} — if the user has an active relationship with product X
     *       (account is open, not suspended, and meets activity criteria)</li>
     *   <li>{@code false} — if:
     *     <ul>
     *       <li>no association exists</li>
     *       <li>the user is inactive for this product</li>
     *       <li>the account is suspended or closed</li>
     *     </ul>
     *   </li>
     * </ul>
     */
    ACTIVE_USER_OF("ACTIVE_USER_OF"),
    /**
     * Compares the aggregated transaction sum against a threshold value with flexible criteria.
     * Used to implement financial rules, eligibility checks, and behavioral analysis.
     * <p>
     * The query evaluates: <i>SUM(transactions of type Y for products of type X) O C</i>
     * where O is one of five comparison operators.
     * <p>
     * Example usage:
     * <code>TRANSACTION_SUM_COMPARE("SAVING", "DEPOSIT", ">", 10000)</code>
     * checks if total deposits to savings accounts exceed $10 000.
     * <p>
     * Parameters:
     * <ul>
     *   <li>arg1: <code>productType</code> (X) &mdash; product category identifier</li>
     *   <li>arg2: <code>transactionType</code> (Y) &mdash; transaction category identifier</li>
     *   <li>arg4: <code>threshold</code> (C) &mdash; numeric comparison value</li>
     * </ul>
     * Operator (O) should be specified separately as part of the query logic.
     *
     * <p>
     * Parameters:
     * <ul>
     *   <li><b>productType</b> — product type identifier (X, first argument)</li>
     *   <li><b>transactionType</b> — transaction type identifier (Y, second argument)</li>
     *   <li><b>comparisonOperator</b> — comparison operator (O, one of >, <, =, >=, <=)</li>
     *   <li><b>threshold</b> — threshold value (C, fourth argument)</li>
     * </ul>
     * <p>
     * Returns:
     * <ul>
     *   <li>boolean result of the comparison operation</li>
     *   <li>{@code false} — if no association exists or the user is inactive for this product</li>
     * </ul>
     */
    TRANSACTION_SUM_COMPARE("TRANSACTION_SUM_COMPARE"),
    /**
     * Compares deposit and withdrawal transaction sums for a specific product category.
     * Used to analyze user financial behavior, liquidity patterns, and product usage intensity.
     * <p>
     * The query evaluates: <i>SUM(DEPOSIT transactions for X) O SUM(WITHDRAW transactions for X)</i>
     * where O is one of five comparison operators.
     * <p>
     * Example usage:
     * <code>TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW("SAVING", ">")</code>
     * checks if total deposits to savings accounts exceed total withdrawals.
     * <p>
     * Parameters:
     * <ul>
     *   <li>arg1: <code>productType</code> (X) — product category identifier</li>
     *   <li>arg2: <code>comparisonOperator</code> (O) — comparison operator</li>
     * </ul>
     * <p>
     * Returns:
     * <ul>
     *   <li>boolean result of the comparison between deposit and withdrawal sums</li>
     * </ul>
     */
    TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");

    private final String type;

    QueryType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
