package ru.rules.dynamicRecommendation.enums;

/**
 * Defines the types of financial transactions that can be performed on an account.
 * This enum is used to categorize and process different kinds of monetary movements.
 */
public enum TransactionType {
    /**
     * A deposit transaction.
     * Indicates an operation where funds are added to a user's account.
     * This increases the account balance.
     * <p>
     * Examples include:
     * <ul>
     *   <li>Transferring money from another account</li>
     *   <li>Cash deposit at an ATM</li>
     *   <li>Receiving a salary payment</li>
     * </ul>
     */
    DEPOSIT("DEPOSIT"),
    /**
     * A withdrawal transaction.
     * Indicates an operation where funds are removed from a user's account.
     * This decreases the account balance.
     * <p>
     * Examples include:
     * <ul>
     *   <li>Withdrawing cash at an ATM</li>
     *   <li>Making a purchase with a debit card</li>
     *   <li>Transferring money to another account</li>
     * </ul>
     */
    WITHDRAW("WITHDRAW");

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TransactionType fromType(String type) {
        for (TransactionType transactionType : values()) {
            if (transactionType.getType().equalsIgnoreCase(type)) {
                return transactionType;
            }
        }
        throw new IllegalArgumentException("No Transaction found for type: " + type);
    }

    @Override
    public String toString() {
        return type;
    }
}
