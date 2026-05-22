package ru.rules.dynamicRecommendation.enums;

/**
 * Defines the types of comparison operations that can be used to evaluate
 * the relationship between two values (e.g., in filtering or validation rules).
 */
public enum ComparisonOperatorType {
    OP_MORE_THAN(">"),        // Greater than (>)
    OP_LESS_THAN("<"),       // Less than (<)
    OP_EQUAL("="),           // Equal to (=)
    OP_MORE_OR_EQUAL(">="),   // Greater than or equal to (>=)
    OP_LESS_OR_EQUAL("<=");    // Less than or equal to (<=)

    private final String operator;

    ComparisonOperatorType(String op) {
        this.operator = op;
    }

    public static ComparisonOperatorType fromType(String op) {
        for (ComparisonOperatorType comparisonOperatorType : values()) {
            if (comparisonOperatorType.getOperator().equalsIgnoreCase(op)) {
                return comparisonOperatorType;
            }
        }
        throw new IllegalArgumentException("No operator found for type: " + op);
    }

    private String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator;
    }
}
