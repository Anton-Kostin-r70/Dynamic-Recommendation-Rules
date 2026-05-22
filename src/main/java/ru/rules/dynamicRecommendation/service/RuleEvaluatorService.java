package ru.rules.dynamicRecommendation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.dto.RuleDTO;
import ru.rules.dynamicRecommendation.enums.QueryType;
import ru.rules.dynamicRecommendation.model.Users;
import ru.rules.dynamicRecommendation.model.query.Query;
import ru.rules.dynamicRecommendation.model.query.QueryFactory;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RuleEvaluatorService {

    private final KnowledgeRepository knowledgeRepository;
    private final RuleService ruleService;
    private final UserService usersService;

    /**
     * Evaluates whether a recommendation rule applies to a specific user by checking all its conditions.
     * <p>
     * The method processes each query in the rule sequentially. If any condition evaluates to {@code false}
     * (after applying negation if specified), the entire rule is considered inapplicable.
     * <p>
     * Evaluation logic:
     * <ol>
     *   <li>If the rule has no queries ({@code getQueriesDTO() == null}), the rule is considered applicable</li>
     *   <li>For each query:
     *     <ul>
     *       <li>Evaluate the base condition using {@link #evaluateCondition(Long, QueryDTO)}</li>
     *       <li>Apply negation if {@link QueryDTO#isNegate()} returns {@code true}</li>
     *       <li>If the final result is {@code false}, immediately return {@code false}</li>
     *     </ul>
     *   </li>
     *   <li>If all conditions are satisfied, return {@code true}</li>
     * </ol>
     * <p>
     * Example: A rule with queries [A, B, C] will return:
     * <ul>
     *   <li>{@code true} — if A=true, B=true, C=true</li>
     *   <li>{@code false} — if A=false (evaluation stops immediately)</li>
     *   <li>{@code false} — if A=true, B=false (evaluation stops at B)</li>
     * </ul>
     *
     * @param ruleDTO the recommendation rule to evaluate, containing a list of queries (conditions)
     * @param userId  the unique identifier of the user for whom the rule is evaluated
     * @return {@code true} if all conditions in the rule are satisfied (after negation);
     * {@code false} if any condition fails or if the rule has no conditions
     * @see RuleDTO#getQueriesDTO()
     * @see QueryDTO#isNegate()
     * @see #evaluateCondition(Long, QueryDTO)
     */
    public boolean evaluate(RuleDTO ruleDTO, Long userId) {
        if (ruleDTO.getQueriesDTO() == null) return true;

        for (QueryDTO queryDTO : ruleDTO.getQueriesDTO()) {
            boolean result = evaluateCondition(userId, queryDTO);
            if (queryDTO.isNegate()) {
                result = !result;
            }

            if (!result) {
                return false;
            }
        }
        return true;
    }

    /**
     * Evaluates a condition for a given user based on the provided query type and arguments.
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Creates an appropriate {@link Query} implementation via {@link QueryFactory#createQuery}</li>
     *   <li>Retrieves the user from the database using {@link UserService#findById}</li>
     *   <li>Delegates the actual evaluation to the created query's {@link Query#evaluate} method</li>
     * </ol>
     * <p>
     * Supported query types:
     * <ul>
     *   <li>{@link QueryType#USER_OF} — checks if the user is associated with a specific product</li>
     *   <li>{@link QueryType#ACTIVE_USER_OF} — checks if the user has an active relationship with a product
     *       (i.e., at least 5 transactions of the specified type)</li>
     *   <li>{@link QueryType#TRANSACTION_SUM_COMPARE} — compares aggregated transaction sum against a threshold
     *       for a specific product and transaction type</li>
     *   <li>{@link QueryType#TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW} — compares deposit and withdrawal sums
     *       for a specific product type</li>
     * </ul>
     *
     * @param userId   the unique identifier of the user for whom the condition is evaluated;
     *                 must not be null
     * @param queryDTO the data transfer object containing query configuration; must not be null.
     *                 The DTO should provide:
     *                 <ul>
     *                   <li>query type via {@code getQuery()} — determines the concrete query implementation</li>
     *                   <li>list of arguments via {@code getArguments()} — parameters required for the specific query</li>
     *                 </ul>
     * @return {@code true} if the condition is met according to the query logic and negation flag;
     * {@code false} otherwise
     * @throws IllegalArgumentException      if:
     *                                       <ul>
     *                                         <li>{@code userId} is null</li>
     *                                         <li>user with the specified {@code userId} does not exist</li>
     *                                         <li>the number of arguments in {@code queryDTO} doesn't match the expected count for the query type</li>
     *                                         <li>an argument cannot be parsed (e.g., invalid number format or enum value)</li>
     *                                       </ul>
     * @throws UnsupportedOperationException if an unknown query type is provided in {@code queryDTO.getQuery()}</li>
     * @throws NullPointerException          if either {@code userId} or {@code queryDTO} is null
     * @see QueryType
     * @see QueryFactory#createQuery(QueryDTO, KnowledgeRepository)
     * @see UserService#findById(Long)
     * @see KnowledgeRepository#isUserOf(Long, String)
     * @see KnowledgeRepository#isActiveUserOf(Long, String)
     * @see KnowledgeRepository#compareTransactionSum(Long, String, String, String, int)
     * @see KnowledgeRepository#compareDepositWithdraw(Long, String, String)
     */
    private boolean evaluateCondition(Long userId, QueryDTO queryDTO) {

        Query query = QueryFactory.createQuery(queryDTO, knowledgeRepository);
        Optional<Users> userOptional = usersService.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return query.evaluate(userId);
    }

    /**
     * Retrieves a list of recommendation rules that are relevant for the specified user
     * by evaluating all available rules against the user's data.
     *
     * @param userId the ID of the user for whom recommendations are generated
     * @return a list of {@link RuleDTO} objects representing applicable recommendation rules
     */
    public List<RuleDTO> getRecommendations(@PathVariable Long userId) {
        List<RuleDTO> allRules = ruleService.getAllRules();
        List<RuleDTO> relevantRules = allRules.stream()
                .filter(dto -> evaluate(dto, userId))
                .collect(Collectors.toList());
        return relevantRules;
    }
}
