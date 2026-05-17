package ru.rules.dynamicRecommendation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rules.dynamicRecommendation.enums.ProductType;
import ru.rules.dynamicRecommendation.enums.TransactionType;
import ru.rules.dynamicRecommendation.model.Transaction;
import ru.rules.dynamicRecommendation.repository.secondary.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer class for managing transaction operations in the dynamic recommendation system.
 * Provides business logic and orchestration for transaction-related operations,
 * including creation, retrieval, and aggregate calculations.
 * Acts as an intermediary between controllers and repositories.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Constructor for dependency injection of TransactionRepository.
     *
     * @param transactionRepository the repository for persistence operations on Transaction entities;
     *                              must not be null
     * @throws IllegalArgumentException if transactionRepository is null
     */
    public TransactionService(TransactionRepository transactionRepository) {
        if (transactionRepository == null) {
            throw new IllegalArgumentException("TransactionRepository cannot be null");
        }
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates and saves a new transaction for a user.
     *
     * @param userId          the unique identifier of the user making the transaction; must not be null or negative
     * @param productType     the product type associated with the transaction; must not be null
     * @param transactionType the type of transaction (DEPOSIT/WITHDRAW); must not be null
     * @param amount          the monetary amount of the transaction; must not be null and positive
     * @return the saved Transaction entity with generated ID and other database‑assigned fields
     * @throws IllegalArgumentException if any required parameter is invalid
     */
    @Transactional
    public Transaction createTransaction(Long userId, ProductType productType,
                                         TransactionType transactionType, BigDecimal amount) {
        // Validate input parameters
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative");
        }
        if (productType == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive and not null");
        }

        Transaction transaction = new Transaction(userId, productType, transactionType, amount);
        return transactionRepository.save(transaction);
    }

    /**
     * Retrieves all transactions for a specific user.
     *
     * @param userId the unique identifier of the user; must not be null or negative
     * @return list of Transaction entities for the user; never null (empty list if no transactions)
     * @throws IllegalArgumentException if userId is invalid
     */
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(Long userId) {
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative");
        }
        return transactionRepository.findByUserId(userId);
    }

    /**
     * Retrieves transactions for a specific user and product type.
     *
     * @param userId      the unique identifier of the user; must not be null or negative
     * @param productType the product type to filter by; must not be null
     * @return list of matching Transaction entities; never null (empty list if no matches)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactionsByProductType(Long userId, ProductType productType) {
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative");
        }
        if (productType == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
        return transactionRepository.findByUserIdAndProductType(userId, productType);
    }

    /**
     * Calculates the total sum of transactions for a user, product type, and transaction type.
     * Returns zero if no matching transactions exist.
     *
     * @param userId          the unique identifier of the user; must not be null or negative
     * @param productType     the product type to filter by; must not be null
     * @param transactionType the transaction type to filter by (DEPOSIT/WITHDRAW); must not be null
     * @return total sum of matching transactions as a long value (converted from BigDecimal)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    @Transactional(readOnly = true)
    public long getUserTransactionSum(Long userId, ProductType productType, TransactionType transactionType) {
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative");
        }
        if (productType == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        Long sum = transactionRepository.sumByUserIdAndProductTypeAndTransactionType(
                userId, productType, transactionType);
        return sum != null ? sum : 0L;
    }

    /**
     * Counts the number of transactions for a specific user and product type.
     *
     * @param userId      the unique identifier of the user; must not be null or negative
     * @param productType the product type to filter by; must not be null
     * @return number of matching transactions; zero if no transactions exist
     * @throws IllegalArgumentException if any parameter is invalid
     */
    @Transactional(readOnly = true)
    public long getUserTransactionCount(Long userId, ProductType productType) {
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("User ID cannot be null or negative");
        }
        if (productType == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
        return transactionRepository.countByUserIdAndProductType(userId, productType);
    }
}