package ru.rules.dynamicRecommendation.model.query;


import ru.rules.dynamicRecommendation.enums.*;
import ru.rules.dynamicRecommendation.model.Users;
import ru.rules.dynamicRecommendation.repository.TransactionRepository;

import java.util.List;

/**
 * Query implementation that checks whether a user has any transactions for a specific product type.
 * A user is considered "of" a product type if they have at least one transaction with that product.
 * <p>
 * Used in recommendation rules to:
 * - Target users who have interacted with a specific product
 * - Exclude users who have never used a particular product type
 * - Build product‑specific recommendation segments
 */
public class UserOfQuery extends Query {

    /**
     * Constructor for creating a UserOfQuery instance.
     *
     * @param arguments list of arguments required for query execution; must contain exactly one element:
     *                  1. Product type as a string (e.g., "DEBIT", "CREDIT") — converted to ProductType enum
     * @param negate    flag indicating whether to negate the evaluation result:
     *                  - false: return true if user has at least one transaction for the product type
     *                  - true: return true if user has NO transactions for the product type
     * @throws IllegalArgumentException if arguments list is null, empty, or contains invalid product type
     */
    public UserOfQuery(List<String> arguments, boolean negate) {
        super(QueryType.USER_OF, arguments, negate);
    }

    /**
     * Evaluates whether the specified user has any transactions for the given product type.
     * <p>
     * The evaluation logic:
     * 1. Extracts the product type from the first argument.
     * 2. Uses the transaction repository to count user's transactions for that product type.
     * 3. Checks if the count is at least 1 (user has interacted with the product).
     * 4. Applies negation if the negate flag is set.
     *
     * @param client                the user to evaluate; must not be null
     * @param transactionRepository repository for accessing transaction data; must not be null
     * @return boolean result of the evaluation:
     * - true: condition is met (user has transactions, or has none if negated)
     * - false: condition is not met (user has no transactions, or has them if negated)
     * @throws IllegalArgumentException if:
     *                                  - client is null
     *                                  - transactionRepository is null
     *                                  - arguments list is empty
     *                                  - the product type argument is invalid (cannot be parsed to ProductType)
     */
    @Override
    public boolean evaluate(Users client, TransactionRepository transactionRepository) {
        // Validate input parameters
        if (client == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (transactionRepository == null) {
            throw new IllegalArgumentException("Transaction repository cannot be null");
        }
        if (arguments == null || arguments.isEmpty()) {
            throw new IllegalArgumentException("Product type argument is missing");
        }

        try {
            // Extract product type from arguments (first argument)
            ProductType productType = ProductType.valueOf(arguments.get(0));

            // Count user's transactions for the specified product type
            long transactionCount = transactionRepository.countByUserIdAndProductType(
                    client.getId(), productType);

            // Determine if user has interacted with the product (at least one transaction)
            boolean hasTransactions = transactionCount >= 1;

            // Apply negation if required
            return negate ? !hasTransactions : hasTransactions;
        } catch (IllegalArgumentException e) {
            // Re‑throw with context if product type parsing fails
            throw new IllegalArgumentException(
                    ("Invalid product type in arguments: " + arguments.get(0)), e);
        }
    }
}
