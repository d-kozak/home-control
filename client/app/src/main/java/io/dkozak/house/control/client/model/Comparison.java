package io.dkozak.house.control.client.model;

public enum Comparison {
    LT,
    LE,
    EQ,
    GE,
    GT;

    @Override
    public String toString() {
        switch (this) {
            case LT:
                return "<";
            case LE:
                return "<=";
            case EQ:
                return "=";
            case GE:
                return ">=";
            case GT:
                return ">";
            default:
                return super.toString();
        }
    }
}
