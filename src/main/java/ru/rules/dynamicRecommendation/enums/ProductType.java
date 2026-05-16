package ru.rules.dynamicRecommendation.enums;
/**
 * Defines the main types of financial products provided by banks and financial institutions.
 * This enum is used to categorize products based on their primary purpose and functionality.
 */
public enum ProductType {
    /**
     * Represents a debit product.
     * Indicates a financial product that gives the user access to their own funds.
     * Typically used for daily transactions and does not involve borrowing.
     * <p>
     * Common examples:
     * <ul>
     *   <li>Current/checking account</li>
     *   <li>Debit card</li>
     *   <li>Prepaid card</li>
     * </ul>
     */
    DEBIT,
    /**
     * Represents a credit product.
     * Indicates a financial product where the user borrows funds from the financial institution,
     * usually with an obligation to repay, often with interest.
     * <p>
     * Common examples:
     * <ul>
     *   <li>Credit card</li>
     *   <li>Personal loan</li>
     *   <li>Mortgage</li>
     *   <li>Overdraft facility</li>
     * </ul>
     */
    CREDIT,
    /**
     * Represents an investment product.
     * Indicates a product designed to help users grow their capital by investing in various assets.
     * Returns are not guaranteed and depend on market performance.
     * <p>
     * Common examples:
     * <ul>
     *   <li>Brokerage account</li>
     *   <li>Mutual funds</li>
     *   <li>Individual Investment Account (IIA)</li>
     *   <li>Stocks and bonds</li>
     * </ul>
     */
    INVEST,
    /**
     * Represents a savings product.
     * Indicates a product intended for securely storing funds and earning interest over time.
     * Focuses on capital preservation with low risk.
     * <p>
     * Common examples:
     * <ul>
     *   <li>Savings account</li>
     *   <li>Time deposit/term deposit</li>
     *   <li>High‑yield savings account</li>
     * </ul>
     */
    SAVING
}
