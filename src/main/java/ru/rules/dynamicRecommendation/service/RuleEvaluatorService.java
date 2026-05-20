package ru.rules.dynamicRecommendation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.rules.dynamicRecommendation.dto.QueryDTO;
import ru.rules.dynamicRecommendation.dto.RuleDTO;
import ru.rules.dynamicRecommendation.repository.KnowledgeRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RuleEvaluatorService {

    private final KnowledgeRepository knowledgeRepository;
    private final RuleService ruleService;

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

    private boolean evaluateCondition(Long userId, QueryDTO queryDTO) {
        return switch (queryDTO.getQuery()) {
            case "USER_OF" -> knowledgeRepository.isUserOf(userId, queryDTO.getArguments().get(0));
            case "ACTIVE_USER_OF" -> knowledgeRepository.isActiveUserOf(userId, queryDTO.getArguments().get(0));
            case "TRANSACTION_SUM_COMPARE" -> {
                var args = queryDTO.getArguments();
                yield knowledgeRepository.compareTransactionSum(
                        userId,
                        args.get(0),
                        args.get(1),
                        args.get(2),
                        Integer.parseInt(args.get(3))
                );
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                var args = queryDTO.getArguments();
                yield knowledgeRepository.compareDepositWithdraw(
                        userId,
                        args.get(0),
                        args.get(1)
                );
            }
            default -> throw new IllegalArgumentException("неизвестный запрос: " + queryDTO.getQuery());
        };
    }

    public List<RuleDTO> getRecommendations(@PathVariable Long userId) {
        List<RuleDTO> allRules = ruleService.getAllRules();
        List<RuleDTO> relevantRules = allRules.stream()
                .filter(dto -> evaluate(dto, userId))
                .collect(Collectors.toList());
        return relevantRules;
    }
}
