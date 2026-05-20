package ru.rules.dynamicRecommendation.repository;

public interface KnowledgeRepository {

    // Verify user has at least 1 transaction by product type.
    boolean isUserOf(Long userId, String productType);

    // Check if user has ≥5 transactions per product type.
    boolean isActiveUserOf(Long userId, String productType);

    // Comparison between the sum of transactions of a particular type and a constant.
    boolean compareTransactionSum(Long userId, String productType, String transactionType, String operator, int constant);

    // Comparison between the sum of deposits and the sum of withdrawals.
    boolean compareDepositWithdraw(Long userId, String productType, String operator);
}