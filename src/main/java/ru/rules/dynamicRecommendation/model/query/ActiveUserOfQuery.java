package ru.rules.dynamicRecommendation.model.query;

import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;
import ru.rules.dynamicRecommendation.repository.TransactionRepository;

import static ru.rules.dynamicRecommendation.enums.QueryType.ACTIVE_USER_OF;

/**
 * Query implementation that checks whether a user is considered "active" for a specific product type.
 * A user is deemed active if they have made at least 5 transactions with the specified product type.
 * <p>
 * Used in recommendation rules to target engaged users or exclude inactive ones. Example use cases:
 * <ul>
 *   <li>Offering premium features to active debit card users</li>
 *   <li>Sending re‑engagement offers to inactive credit card holders</li>
 * </ul>
 * <p>
 * The activity threshold (5 transactions) is hardcoded in the business logic of {@link KnowledgeRepository#isActiveUserOf}.
 *
 * @see KnowledgeRepository#isActiveUserOf
 */
public class ActiveUserOfQuery extends Query {

    private final KnowledgeRepository knowledgeRepository;

    /**
     * Constructor for creating an {@code ActiveUserOfQuery} instance from a QueryDTO object.
     *
     * @param queryDTO             data transfer object containing query configuration; must not be null.
     *                             The DTO should provide:
     *                             <ul>
     *                             <li>{@code query} — must be "ACTIVE_USER_OF" (validated by the parent constructor)</li>
     *                             <li>{@code arguments} — list of string parameters; must contain exactly one element:
     *                                 <ul>
     *                                 <li>Product type as a string (e.g., "DEBIT", "CREDIT") — converted to {@link ProductType} enum
     *                                     by the underlying repository</li>
     *                                 </ul>
     *                             </li>
     *                             <li>{@code negate} — flag indicating whether to negate the evaluation result:
     *                                 <ul>
     *                                 <li><b>false</b>: return {@code true} if user is active (≥5 transactions with the product type)</li>
     *                                 <li><b>true</b>: return {@code true} if user is NOT active (<5 transactions with the product type)</li>
     *                                 </ul>
     *                             </li>
     *                             </ul>
     * @param aKnowledgeRepository repository providing business logic for user activity checks;
     *                             must not be null. This repository will be used in the {@link #evaluate} method
     *                             to determine user activity status.
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                  <li>{@code queryDTO} is null</li>
     *                                  <li>the query type extracted from {@code queryDTO.getQuery()} is not "ACTIVE_USER_OF"</li>
     *                                  <li>the arguments list ({@code queryDTO.getArguments()}) is null or empty</li>
     *                                  <li>the product type argument is invalid (cannot be parsed to {@link ProductType})</li>
     *                                  </ul>
     * @throws NullPointerException     if {@code aKnowledgeRepository} is null
     */
    public ActiveUserOfQuery(QueryDTO queryDTO, KnowledgeRepository aKnowledgeRepository) {
        super(queryDTO);
        this.knowledgeRepository = aKnowledgeRepository;
        validateArguments(1, ACTIVE_USER_OF);
    }

    /**
     * Evaluates whether the specified user is active for the given product type by counting their transactions.
     * The evaluation follows this logic:
     * <ol>
     *   <li>Extracts the product type from the first argument ({@code arguments.get(0)}).</li>
     *   <li>Uses {@link KnowledgeRepository} to count user's transactions for that product type.</li>
     *   <li>Checks if the count is at least 5 (activity threshold).</li>
     *   <li>Applies negation if the {@code negate} flag is set.</li>
     * </ol>
     *
     * @param userId                  the user to evaluate; must not be null. The user's ID is used for transaction lookup.
     * @param transactionRepository repository for accessing transaction data; must not be null.
     *                              Note: This parameter is currently not used directly by this query —
     *                              the logic is delegated to {@link KnowledgeRepository}.
     * @return boolean result of the evaluation:
     * <ul>
     * <li><b>true</b>: condition is met
     *     <ul>
     *     <li>user is active (≥5 transactions) when {@code negate = false}</li>
     *     <li>user is not active (<5 transactions) when {@code negate = true}</li>
     *     </ul>
     * </li>
     * <li><b>false</b>: condition is not met
     *     <ul>
     *     <li>user is not active when {@code negate = false}</li>
     *     <li>user is active when {@code negate = true}</li>
     *     </ul>
     * </li>
     * </ul>
     * @throws IllegalArgumentException if {@code user} is null or if arguments are invalid
     * @throws RuntimeException         if database access fails in {@link KnowledgeRepository}
     */
    @Override
    public boolean evaluate(Long userId, TransactionRepository transactionRepository) {
        boolean result = knowledgeRepository.isActiveUserOf(userId, arguments.get(0));
        return negate != result;
    }
}