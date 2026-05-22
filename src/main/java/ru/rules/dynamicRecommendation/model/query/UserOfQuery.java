package ru.rules.dynamicRecommendation.model.query;


import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.*;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;

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
     * Evaluates whether the specified user has any transactions for the given product type.
     * <p>
     * The evaluation logic:
     * 1. Extracts the product type from the first argument ({@code arguments.get(0)}).
     * 2. Uses {@link KnowledgeRepository} to check if the user has transactions for that product type
     *    via the {@code isUserOf} method.
     * 3. The check returns {@code true} if the user has at least one transaction for the product.
     * 4. Applies negation if the {@code negate} flag is set.
     *
     * @param userId the ID of the user to evaluate; must not be null
     * @return boolean result of the evaluation:
     * <ul>
     * <li><b>true</b>: condition is met
     *   <ul>
     *   <li>user has at least one transaction for the product type when {@code negate = false}</li>
     *   <li>user has no transactions for the product type when {@code negate = true}</li>
     *   </ul>
     * </li>
     * <li><b>false</b>: condition is not met
     *   <ul>
     *   <li>user has no transactions for the product type when {@code negate = false}</li>
     *   <li>user has at least one transaction for the product type when {@code negate = true}</li>
     *   </ul>
     * </li>
     * </ul>
     *
     * @throws IllegalArgumentException if:
     *   - {@code userId} is null
     *   - {@code arguments} list is empty
     *   - the product type argument is invalid (null, empty, or cannot be parsed to a valid {@link ProductType})
     * @throws RuntimeException if database access fails during the transaction check
     *        (e.g., connection issues, query execution errors, or data retrieval problems)
     */
    @Override
    public boolean evaluate(Long userId) {
        boolean result = knowledgeRepository.isUserOf(userId, ProductType.fromType(arguments.get(0)));
        return negate != result;
    }
}
