package ru.rules.dynamicRecommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.TransactionType;
import ru.rules.dynamicRecommendation.model.Transaction;

import java.util.List;

/**
 * Repository interface for managing Transaction entities.
 * Provides CRUD operations and custom query methods for transaction data access.
 * Extends JpaRepository to leverage Spring Data JPA functionality.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Counts the number of transactions for a specific user and product type.
     *
     * @param userId      the identifier of the user whose transactions are being counted
     * @param productType the type of product to filter transactions by
     * @return the total count of matching transactions (0 if none found)
     */
    long countByUserIdAndProductType(Long userId, ProductType productType);

    /**
     * Calculates the sum of transaction amounts for a specific user, product type,
     * and transaction type.
     * Returns 0 if no matching transactions are found (COALESCE function in SQL).
     *
     * @param userId          the identifier of the user whose transaction amounts are being summed
     * @param productType     the type of product to filter transactions by
     * @param transactionType the type of transaction (DEPOSIT/WITHDRAW) to filter by
     * @return the sum of all matching transaction amounts as a long value
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.userId = :userId AND t.productType = :productType AND t.transactionType = :transactionType")
    long sumByUserIdAndProductTypeAndTransactionType(
            Long userId, ProductType productType, TransactionType transactionType);

    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param userId the identifier of the user whose transactions are being retrieved
     * @return a list of Transaction entities belonging to the specified user
     * (empty list if no transactions found)
     */
    List<Transaction> findByUserId(Long userId);

    /**
     * Retrieves transactions for a specific user filtered by product type.
     *
     * @param userId      the identifier of the user whose transactions are being retrieved
     * @param productType the type of product to filter transactions by
     * @return a list of Transaction entities matching the criteria
     * (empty list if no matching transactions found)
     */
    List<Transaction> findByUserIdAndProductType(Long userId, ProductType productType);

    /**
     * Retrieves transactions for a specific user filtered by product type
     * and transaction type.
     *
     * @param userId          the identifier of the user whose transactions are being retrieved
     * @param productType     the type of product to filter transactions by
     * @param transactionType the type of transaction (DEPOSIT/WITHDRAW) to filter by
     * @return a list of Transaction entities matching all criteria
     * (empty list if no matching transactions found)
     */
    List<Transaction> findByUserIdAndProductTypeAndTransactionType(
            Long userId, ProductType productType, TransactionType transactionType);
}