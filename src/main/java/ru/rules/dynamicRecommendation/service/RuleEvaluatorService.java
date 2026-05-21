package ru.rules.dynamicRecommendation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.dto.RuleDTO;
import ru.rules.dynamicRecommendation.enums.QueryType;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RuleEvaluatorService {

    private final KnowledgeRepository knowledgeRepository;
    private final RuleService ruleService;

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
     * The method determines which condition to check by analyzing the query type, validates
     * the number of arguments, and delegates the actual evaluation to the knowledge repository.
     * <p>
     * Supported query types:
     * <ul>
     *   <li>{@link QueryType#USER_OF} — checks if the user is associated with a specific product</li>
     *   <li>{@link QueryType#ACTIVE_USER_OF} — checks if the user has an active relationship with a product</li>
     *   <li>{@link QueryType#TRANSACTION_SUM_COMPARE} — compares aggregated transaction sum against a threshold</li>
     *   <li>{@link QueryType#TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW} — compares deposit and withdrawal sums</li>
     * </ul>
     *
     * @param userId   the unique identifier of the user for whom the condition is evaluated
     * @param queryDTO the data transfer object containing:
     *                 <ul>
     *                   <li>query type ({@code getQuery()})</li>
     *                   <li>list of arguments ({@code getArguments()})</li>
     *                 </ul>
     * @return {@code true} if the condition is met; {@code false} otherwise
     * @throws IllegalArgumentException      if the number of arguments doesn't match the expected count
     *                                       or if an argument cannot be parsed (e.g., invalid number format)
     * @throws UnsupportedOperationException if an unknown query type is provided
     * @see QueryType
     * @see KnowledgeRepository#isUserOf(Long, String)
     * @see KnowledgeRepository#isActiveUserOf(Long, String)
     * @see KnowledgeRepository#compareTransactionSum(Long, String, String, String, int)
     * @see KnowledgeRepository#compareDepositWithdraw(Long, String, String)
     */
    private boolean evaluateCondition(Long userId, QueryDTO queryDTO) {
        return switch (QueryType.valueOf(queryDTO.getQuery())) {
            case USER_OF -> {
                validateArguments(queryDTO, 1, "USER_OF");
                yield knowledgeRepository.isUserOf(userId, queryDTO.getArguments().get(0));
            }
            case ACTIVE_USER_OF -> {
                validateArguments(queryDTO, 1, "ACTIVE_USER_OF");
                yield knowledgeRepository.isActiveUserOf(userId, queryDTO.getArguments().get(0));
            }
            case TRANSACTION_SUM_COMPARE -> {
                validateArguments(queryDTO, 4, "TRANSACTION_SUM_COMPARE");
                var args = queryDTO.getArguments();
                yield knowledgeRepository.compareTransactionSum(
                        userId,
                        args.get(0),
                        args.get(1),
                        args.get(2),
                        Integer.parseInt(args.get(3))
                );
            }
            case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW -> {
                validateArguments(queryDTO, 2, "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");
                var args = queryDTO.getArguments();
                yield knowledgeRepository.compareDepositWithdraw(
                        userId,
                        args.get(0),
                        args.get(1)
                );
            }
            default -> throw new UnsupportedOperationException("неизвестный запрос: " + queryDTO.getQuery());
        };
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

    /**
     * Validates that the number of arguments matches the expected count for the query type.
     *
     * @param queryDTO      DTO containing the query and its arguments
     * @param expectedCount the expected number of arguments
     * @param queryName     the query name for the error message
     * @throws IllegalArgumentException if the number of arguments does not match the expected count
     */
    private void validateArguments(QueryDTO queryDTO, int expectedCount, String queryName) {
        int actualCount = queryDTO.getArguments().size();
        if (actualCount != expectedCount) {
            throw new IllegalArgumentException(
                    String.format("Запрос %s ожидает %d аргументов, но получено %d",
                            queryName, expectedCount, actualCount)
            );
        }
    }
}
