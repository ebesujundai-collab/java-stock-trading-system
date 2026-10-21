package com.example.trading.validator;

public enum Side {
    B,
    S;

    public String getDisplayName() {
        if (this == B) {
            return "Buy";
        }
        return "Sell";
    }
}
