package com.example.trading.validator;

/** 売買区分を2種類に限定する。 */
public enum TradeType {
    B,
    S;

    public static TradeType fromInput(String input) {
        if ("B".equals(input)) {
            return B;
        }
        if ("S".equals(input)) {
            return S;
        }
        IllegalArgumentException error = new IllegalArgumentException("売買区分はB、Sを入力");

        throw error;
    }
}
