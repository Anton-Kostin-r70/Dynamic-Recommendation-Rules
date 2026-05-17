package ru.rules.dynamicRecommendation.model.query;


import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.QueryType;
import ru.rules.dynamicRecommendation.model.Users;
import ru.rules.dynamicRecommendation.repository.secondary.TransactionRepository;

import java.util.List;

/**
 * Query implementation that checks whether a user is considered "active" for a specific product type.
 * A user is deemed active if they have made at least 5 transactions with the specified product type.
 * <p>
 * Used in recommendation rules to target engaged users or exclude inactive ones.
 */
public class ActiveUserOfQuery extends Query {

    /**
     * Constructor for creating an ActiveUserOfQuery instance.
     *
     * @param arguments list of arguments required for query execution;
     *                  must contain exactly one element — the product type as a string
     *                  (e.g., "DEBIT", "CREDIT") that will be converted to ProductType enum
     * @param negate    flag indicating whether to negate the evaluation result:
     *                  - false: return true if user is active (≥5 transactions)
     *                  - true: return true if user is NOT active (<5 transactions)
     * @throws IllegalArgumentException if arguments list is null, empty, or contains invalid product type
     */
    public ActiveUserOfQuery(List<String> arguments, boolean negate) {
        super(QueryType.ACTIVE_USER_OF, arguments, negate);
    }

    /**
     * Evaluates whether the specified user is active for the given product type
     * by counting their transactions.
     * The evaluation logic:
     * 1. Extracts the product type from the first argument.
     * 2. Uses the transaction repository to count user's transactions for that product type.
     * 3. Checks if the count is at least 5.
     * 4. Applies negation if the negate flag is set.
     *
     * @param user                  the user to evaluate; must not be null
     * @param transactionRepository repository for accessing transaction data; must not be null
     * @return boolean result of the evaluation:
     * - true: condition is met (user is active, or not active if negated)
     * - false: condition is not met (user is not active, or is active if negated)
     * @throws IllegalArgumentException if:
     *                                  - user is null
     *                                  - transactionRepository is null
     *                                  - arguments list is empty
     *                                  - the product type argument is invalid (cannot be parsed to ProductType)
     * @throws RuntimeException         if database access fails
     */
    @Override
    public boolean evaluate(Users user, TransactionRepository transactionRepository) {
        // Validate input parameters
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (transactionRepository == null) {
            throw new IllegalArgumentException("Transaction repository cannot be null");
        }
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Product type argument is missing");
        }

        try {
            // Extract product type from arguments (first argument)
            ProductType productType = ProductType.valueOf(arguments.get(0));

            // Count user's transactions for the specified product type
            long transactionCount = transactionRepository.countByUserIdAndProductType(
                    user.getId(), productType);

            // Determine if user is active (at least 5 transactions)
            boolean isActive = transactionCount >= 5;

            // Apply negation if required
            return negate ? !isActive : isActive;
        } catch (IllegalArgumentException e) {
            // Re‑throw with context if product type parsing fails
            throw new IllegalArgumentException(
                    ("Invalid product type in arguments: " + arguments.get(0)), e);
        }
    }
}