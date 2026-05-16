package ru.rules.dynamicRecommendation.enums;

/**
 * Defines the types of comparison operations that can be used to evaluate
 * the relationship between two values (e.g., in filtering or validation rules).
 */
public enum ComparisonOperatorType {
    OP_MORE_THAN,        // Greater than (>)
    OP_LESS_THAN,       // Less than (<)
    OP_EQUAL,           // Equal to (=)
    OP_MORE_OR_EQUAL,   // Greater than or equal to (>=)
    OP_LESS_OR_EQUAL    // Less than or equal to (<=)
}
