package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.enums.ComparisonOperatorType;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.QueryType;
import ru.rules.dynamicRecommendation.enums.TransactionType;
import ru.rules.dynamicRecommendation.model.User;
import ru.rules.dynamicRecommendation.repository.TransactionRepository;

import java.util.Arrays;
import java.util.List;

/**
 * Query implementation that compares deposit and withdrawal sums for a specific product type.
 * Evaluates whether the relationship between deposits and withdrawals matches the specified operator.
 * <p>
 * Used in recommendation rules to target users based on their transaction patterns:
 * - Users with high deposits relative to withdrawals
 * - Users with balanced deposit/withdrawal activity
 * - Users with high withdrawals relative to deposits
 */
public class TransactionSumCompareDepositWithdrawQuery extends Query {

    /**
     * Constructor for creating a TransactionSumCompareDepositWithdrawQuery instance.
     *
     * @param arguments list of arguments required for query execution; must contain exactly two elements:
     *                  1. Product type as a string (e.g., "DEBIT", "CREDIT") — converted to ProductType enum
     *                  2. Comparison operator as a string (e.g., ">", "<", "=") — converted to ComparisonOperatorType enum
     * @param negate    flag indicating whether to negate the evaluation result:
     *                  - false: return true if the comparison condition is met
     *                  - true: return true if the comparison condition is NOT met
     * @throws IllegalArgumentException if arguments list is null, doesn't have exactly 2 elements,
     *                                  or contains invalid product type/operator
     */
    public TransactionSumCompareDepositWithdrawQuery(List<String> arguments, boolean negate) {
        super(QueryType.TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW, arguments, negate);
    }

    /**
     * Evaluates the condition by comparing deposit and withdrawal transaction sums for a user's product.
     * <p>
     * The evaluation logic:
     * 1. Extracts product type and comparison operator from arguments.
     * 2. Retrieves total deposit and withdrawal amounts for the product type.
     * 3. Compares the sums using the specified operator.
     * 4. Applies negation if the negate flag is set.
     *
     * @param user                  the user to evaluate; must not be null
     * @param transactionRepository repository for accessing transaction data; must not be null
     * @return boolean result of the evaluation:
     * - true: condition is met (comparison passes, or fails if negated)
     * - false: condition is not met (comparison fails, or passes if negated)
     * @throws IllegalArgumentException if:
     *                                  - user is null
     *                                  - transactionRepository is null
     *                                  - arguments list doesn't contain exactly 2 elements
     *                                  - product type or operator argument is invalid
     * @throws RuntimeException         if database access fails during sum calculations
     */
    @Override
    public boolean evaluate(User user, TransactionRepository transactionRepository) {
        // Validate input parameters
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (transactionRepository == null) {
            throw new IllegalArgumentException("Transaction repository cannot be null");
        }
        if (arguments == null || arguments.size() != 2) {
            throw new IllegalArgumentException(
                    "Arguments must contain exactly 2 elements: productType and operator");
        }

        try {
            // Extract parameters from arguments
            ProductType productType = ProductType.valueOf(arguments.get(0));
            ComparisonOperatorType operator = parseOperator(arguments.get(1));

            // Calculate total deposit and withdrawal amounts
            long depositSum = transactionRepository.sumByUserIdAndProductTypeAndTransactionType(
                    user.getId(), productType, TransactionType.DEPOSIT);
            long withdrawSum = transactionRepository.sumByUserIdAndProductTypeAndTransactionType(
                    user.getId(), productType, TransactionType.WITHDRAW);

            // Compare sums using the specified operator
            boolean comparisonResult = compare(depositSum, withdrawSum, operator);

            // Apply negation if required
            return negate ? !comparisonResult : comparisonResult;
        } catch (IllegalArgumentException e) {
            // Re‑throw with context if parsing fails
            throw new IllegalArgumentException("Error in query evaluation: " + e.getMessage(), e);
        }
    }

    /**
     * Parses a string representation of a comparison operator into the corresponding enum value.
     *
     * @param operatorStr string representation of the operator (e.g., ">", "<", "=")
     * @return corresponding ComparisonOperatorType enum value
     * @throws IllegalArgumentException if the operator string is not recognized
     */
    private ComparisonOperatorType parseOperator(String operatorStr) {
        return Arrays.stream(ComparisonOperatorType.values())
                .filter(op -> op.getOperator().equals(operatorStr))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown operator: " + operatorStr));
    }

    /**
     * Compares two sum values using the specified comparison operator.
     *
     * @param depositSum  total amount of deposits
     * @param withdrawSum total amount of withdrawals
     * @param operator    the comparison operator to use
     * @return result of the comparison (true if condition is met, false otherwise)
     * @throws IllegalArgumentException if an unknown operator is provided
     */
    private boolean compare(long depositSum, long withdrawSum, ComparisonOperatorType operator) {
        switch (operator) {
            case OP_MORE_THAN:
                return depositSum > withdrawSum;
            case OP_LESS_THAN:
                return depositSum < withdrawSum;
            case OP_EQUAL:
                return depositSum == withdrawSum;
            case OP_MORE_OR_EQUAL:
                return depositSum >= withdrawSum;
            case OP_LESS_OR_EQUAL:
                return depositSum <= withdrawSum;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}
