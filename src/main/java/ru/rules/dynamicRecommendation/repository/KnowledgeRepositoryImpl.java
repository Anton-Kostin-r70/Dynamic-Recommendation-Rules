package ru.rules.dynamicRecommendation.repository;

import com.github.benmanes.caffeine.cache.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

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
    public boolean isUserOf(Long userId, String productType) {
        String key = userId + "_" + productType;
        return Boolean.TRUE.equals(userOfCache.get(key, k -> {
            String sql = """
                        SELECT EXISTS (
                        SELECT 1 FROM transactions t
                        WHERE t.user_id = ? AND t.product_type = ?
                            LIMIT 1
                        )
                    """;
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType));
        }));
    }

    @Override
    public boolean isActiveUserOf(Long userId, String productType) {
        String key = userId + "_" + productType;
        return Boolean.TRUE.equals(activeUserOfCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) >= 5 FROM transactions t
                    WHERE t.user_id = ? AND t.product_type = ?
                    """;
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType));
        }));
    }

    @Override
    public boolean compareTransactionSum(Long userId, String productType,
                                         String transactionType, String operator, int constant) {
        String key = userId + "_" + productType + "_" + transactionType + "_" + operator + "_" + constant;
        return Boolean.TRUE.equals(transactionSumCache.get(key, k -> {
            String sql = """
                    SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
                    WHERE t.user_id = ?
                      AND t.product_type = ?
                      AND t.transaction_type = ?
                    """;
            Long sum = jdbcTemplate.queryForObject(sql, Long.class, userId, productType, transactionType);

            long actualSum = (sum == null) ? 0L : sum;

            return switch (operator) {
                case ">" -> actualSum > constant;
                case "<" -> actualSum < constant;
                case ">=" -> actualSum >= constant;
                case "<=" -> actualSum <= constant;
                case "=" -> actualSum == constant;
                default -> throw new IllegalArgumentException("Некорректный оператор: " + operator);
            };
        }));
    }

    @Override
    public boolean compareDepositWithdraw(Long userId, String productType, String operator) {
        String key = userId + "_" + productType + "_" + operator;
        return Boolean.TRUE.equals(depositWithdrawCache.get(key, k -> {
            // DEPOSIT сумма
            String depositSql = """
                        SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
                        WHERE t.user_id = ?
                          AND t.product_type = ?
                          AND t.transaction_type = 'DEPOSIT'
                    """;

            // WITHDRAW сумма
            String withdrawSql = """
                        SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
                        WHERE t.user_id = ?
                          AND t.product_type = ?
                          AND t.transaction_type = 'WITHDRAW'
                    """;

            Long depositSum = jdbcTemplate.queryForObject(depositSql, Long.class, userId, productType);
            Long withdrawSum = jdbcTemplate.queryForObject(withdrawSql, Long.class, userId, productType);

            long deposit = (depositSum == null) ? 0L : depositSum;
            long withdraw = (withdrawSum == null) ? 0L : withdrawSum;

            return switch (operator) {
                case ">" -> deposit > withdraw;
                case "<" -> deposit < withdraw;
                case ">=" -> deposit >= withdraw;
                case "<=" -> deposit <= withdraw;
                case "=" -> deposit == withdraw;
                default -> throw new IllegalArgumentException("Unknown operator: " + operator);
            };
        }));
    }
}