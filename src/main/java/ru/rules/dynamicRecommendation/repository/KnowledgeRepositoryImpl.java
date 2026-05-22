package ru.rules.dynamicRecommendation.repository;

import com.github.benmanes.caffeine.cache.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.rules.dynamicRecommendation.enums.ComparisonOperatorType;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.TransactionType;

import java.util.concurrent.TimeUnit;

import static ru.rules.dynamicRecommendation.enums.TransactionType.DEPOSIT;
import static ru.rules.dynamicRecommendation.enums.TransactionType.WITHDRAW;

@Repository
@RequiredArgsConstructor
public class KnowledgeRepositoryImpl implements KnowledgeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final Cache<String, Boolean> userOfCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private final Cache<String, Boolean> activeUserOfCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private final Cache<String, Boolean> transactionSumCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private final Cache<String, Boolean> depositWithdrawCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    @Override
    public boolean isUserOf(Long userId, ProductType productType) {
        String key = userId + "_" + productType.getType();
        return Boolean.TRUE.equals(userOfCache.get(key, k -> {
            String sql = """
                        SELECT EXISTS (
                        SELECT 1 FROM transactions t
                        WHERE t.user_id = ? AND t.product_type = ?
                            LIMIT 1
                        )
                    """;
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType.getType()));
        }));
    }

    @Override
    public boolean isActiveUserOf(Long userId, ProductType productType) {
        String key = userId + "_" + productType;
        return Boolean.TRUE.equals(activeUserOfCache.get(key, k -> {
            String sql = """
            SELECT COUNT(*) >= 5 FROM transactions t
            WHERE t.user_id = ? AND t.product_type = ?
        """;
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType.getType()));
        }));
    }

    @Override
    public boolean compareTransactionSum(Long userId, ProductType productType,
                                         TransactionType transactionType, ComparisonOperatorType operator, int constant) {
        String key = userId + "_" + productType.getType() + "_" + transactionType.getType() + "_" + operator + "_" + constant;
        return Boolean.TRUE.equals(transactionSumCache.get(key, k -> {
            long actualSum = getSum(userId, productType, transactionType);
            return performComparison(actualSum, operator, constant);
        }));
    }

    @Override
    public boolean compareDepositWithdraw(Long userId, ProductType productType, ComparisonOperatorType operator) {
        String key = userId + "_" + productType + "_" + operator;
        return Boolean.TRUE.equals(depositWithdrawCache.get(key, k -> {
            long depositSum = getSum(userId, productType, DEPOSIT);
            long withdrawSum = getSum(userId, productType, WITHDRAW);
            return performComparison(depositSum, operator, withdrawSum);
        }));
    }

    private Long getSum(Long userId, ProductType productType, TransactionType transactionType) {
        String querySQL = """
                SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
                WHERE t.user_id = ?
                  AND t.product_type = ?
                  AND t.transaction_type = ?
                """;
        return jdbcTemplate.queryForObject(querySQL, Long.class, userId, productType.getType(), transactionType.getType());
    }

    /**
     * Performs a comparison between two values using the specified operator.
     *
     * @param value     the actual value to compare
     * @param operator  the comparison operator to use
     * @param threshold the threshold value to compare against
     * @return true if the comparison condition is met, false otherwise
     * @throws IllegalArgumentException if an unknown operator is provided
     */
    private boolean performComparison(long value, ComparisonOperatorType operator, long threshold) {
        return switch (operator) {
            case OP_MORE_THAN -> value > threshold;
            case OP_LESS_THAN -> value < threshold;
            case OP_EQUAL -> value == threshold;
            case OP_MORE_OR_EQUAL -> value >= threshold;
            case OP_LESS_OR_EQUAL -> value <= threshold;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}