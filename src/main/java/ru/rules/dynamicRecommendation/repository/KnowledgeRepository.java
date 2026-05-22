package ru.rules.dynamicRecommendation.repository;

import ru.rules.dynamicRecommendation.enums.ComparisonOperatorType;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.TransactionType;

public interface KnowledgeRepository {

    // Verify user has at least 1 transaction by product type.
    boolean isUserOf(Long userId, ProductType productType);

    // Check if user has ≥5 transactions per product type.
    boolean isActiveUserOf(Long userId, ProductType productType);

    // Comparison between the sum of transactions of a particular type and a constant.
    boolean compareTransactionSum(Long userId, ProductType productType,
                                  TransactionType transactionType, ComparisonOperatorType operator, int constant);

    // Comparison between the sum of deposits and the sum of withdrawals.
    boolean compareDepositWithdraw(Long userId, ProductType productType, ComparisonOperatorType operator);
}