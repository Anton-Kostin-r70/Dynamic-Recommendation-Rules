package enums;

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
    DEPOSIT,
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
    WITHDRAW
}
