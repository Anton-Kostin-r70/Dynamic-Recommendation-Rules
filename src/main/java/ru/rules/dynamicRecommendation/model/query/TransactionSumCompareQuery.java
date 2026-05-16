package ru.rules.dynamicRecommendation.model.query;


import ru.rules.dynamicRecommendation.enums.ComparisonOperatorType;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.QueryType;
import ru.rules.dynamicRecommendation.enums.TransactionType;
import ru.rules.dynamicRecommendation.model.Users;
import ru.rules.dynamicRecommendation.repository.TransactionRepository;

import java.util.Arrays;
import java.util.List;

/**
 * Query implementation that compares a user's transaction sum for a specific product and transaction type
 * against a constant value using a specified comparison operator.
 * Used in recommendation rules to target users based on transaction volume thresholds:
 * - Users with high deposit amounts
 * - Users exceeding minimum transaction thresholds
 * - Users with low withdrawal activity
 */
public class TransactionSumCompareQuery extends Query {

    /**
     * Constructor for creating a TransactionSumCompareQuery instance.
     *
     * @param arguments list of arguments required for query execution; must contain exactly four elements:
     *                  1. Product type as a string (e.g., "DEBIT", "CREDIT") — converted to ProductType enum
     *                  2. Transaction type as a string ("DEPOSIT" or "WITHDRAW") — converted to TransactionType enum
     *                  3. Comparison operator as a string (e.g., ">", "<", "=") — converted to ComparisonOperatorType enum
     *                  4. Constant value as a string — converted to long, representing the threshold amount
     * @param negate    flag indicating whether to negate the evaluation result:
     *                  - false: return true if the comparison condition is met
     *                  - true: return true if the comparison condition is NOT met
     * @throws IllegalArgumentException if arguments list is null, doesn't have exactly 4 elements,
     *                                  or contains invalid values for any of the parameters
     */
    public TransactionSumCompareQuery(List<String> arguments, boolean negate) {
        super(QueryType.TRANSACTION_SUM_COMPARE, arguments, negate);
    }

    /**
     * Evaluates the condition by comparing a user's transaction sum against a constant threshold.
     * The evaluation logic:
     * 1. Extracts product type, transaction type, operator, and constant from arguments.
     * 2. Retrieves the total transaction sum for the specified product and transaction types.
     * 3. Compares the sum against the constant using the specified operator.
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
     *                                  - arguments list doesn't contain exactly 4 elements
     *                                  - any argument is invalid (unparsable product type, transaction type, operator, or constant)
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
        if (arguments == null || arguments.size() != 4) {
            throw new IllegalArgumentException(
                    "Arguments must contain exactly 4 elements: productType, transactionType, operator, constant");
        }
        try {
            // Extract parameters from arguments
            ProductType productType = ProductType.valueOf(arguments.get(0));
            TransactionType transactionType = TransactionType.valueOf(arguments.get(1));
            ComparisonOperatorType operator = parseOperator(arguments.get(2));
            long constant = Long.parseLong(arguments.get(3));

            // Calculate total transaction sum
            long sum = transactionRepository.sumByUserIdAndProductTypeAndTransactionType(
                    user.getId(), productType, transactionType);

            // Compare sum against constant using specified operator
            boolean comparisonResult = compare(sum, constant, operator);

            // Apply negation if required
            return negate ? !comparisonResult : comparisonResult;
        } catch (IllegalArgumentException e) {
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
     * Compares a transaction sum against a constant value using the specified comparison operator.
     *
     * @param sum      total transaction amount to compare
     * @param constant threshold value to compare against
     * @param operator the comparison operator to use
     * @return result of the comparison (true if condition is met, false otherwise)
     * @throws IllegalArgumentException if an unknown operator is provided
     */
    private boolean compare(long sum, long constant, ComparisonOperatorType operator) {
        switch (operator) {
            case OP_MORE_THAN:
                return sum > constant;
            case OP_LESS_THAN:
                return sum < constant;
            case OP_EQUAL:
                return sum == constant;
            case OP_MORE_OR_EQUAL:
                return sum >= constant;
            case OP_LESS_OR_EQUAL:
                return sum <= constant;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}