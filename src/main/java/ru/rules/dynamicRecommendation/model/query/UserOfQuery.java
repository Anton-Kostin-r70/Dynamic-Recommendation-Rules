package ru.rules.dynamicRecommendation.model.query;


import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.*;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;
import ru.rules.dynamicRecommendation.repository.TransactionRepository;

import static ru.rules.dynamicRecommendation.enums.QueryType.USER_OF;

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

    private final KnowledgeRepository knowledgeRepository;

    /**
     * Constructor for creating a {@code UserOfQuery} instance from a QueryDTO object.
     *
     * @param queryDTO            data transfer object containing query configuration; must not be null.
     *                            The DTO should provide:
     *                            <ul>
     *                            <li>{@code query} — must be "USER_OF" (will be validated by the parent constructor)</li>
     *                            <li>{@code arguments} — list of string parameters; must contain exactly one element:
     *                                <ul>
     *                                <li>Product type as a string (e.g., "DEBIT", "CREDIT") — converted to {@link ProductType} enum</li>
     *                                </ul>
     *                            </li>
     *                            <li>{@code negate} — flag indicating whether to negate the evaluation result</li>
     *                            </ul>
     * @param knowledgeRepository repository providing business logic for user‑product relationship checks;
     *                            must not be null
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                  <li>{@code queryDTO} is null</li>
     *                                  <li>the query type extracted from {@code queryDTO.getQuery()} is not "USER_OF"</li>
     *                                  <li>the arguments list ({@code queryDTO.getArguments()}) is null or empty</li>
     *                                  <li>the product type argument is invalid (cannot be parsed to {@link ProductType})</li>
     *                                  </ul>
     * @throws NullPointerException     if {@code knowledgeRepository} is null
     */
    public UserOfQuery(QueryDTO queryDTO, KnowledgeRepository knowledgeRepository) {
        super(queryDTO);
        this.knowledgeRepository = knowledgeRepository;
        validateArguments(1, USER_OF);
    }

    /**
     * Evaluates whether the specified userId has any transactions for the given product type.
     * <p>
     * The evaluation logic:
     * 1. Extracts the product type from the first argument.
     * 2. Uses the transaction repository to count userId's transactions for that product type.
     * 3. Checks if the count is at least 1 (userId has interacted with the product).
     * 4. Applies negation if the negate flag is set.
     *
     * @param userId                  the userId to evaluate; must not be null
     * @param transactionRepository repository for accessing transaction data; must not be null
     * @return boolean result of the evaluation:
     * - true: condition is met (userId has transactions, or has none if negated)
     * - false: condition is not met (userId has no transactions, or has them if negated)
     * @throws IllegalArgumentException if:
     *                                  - client is null
     *                                  - transactionRepository is null
     *                                  - arguments list is empty
     *                                  - the product type argument is invalid (cannot be parsed to ProductType)
     */
    @Override
    public boolean evaluate(Long userId, TransactionRepository transactionRepository) {
        boolean result = knowledgeRepository.isUserOf(userId, arguments.get(0));
        return negate ? !result : result;
    }
}
