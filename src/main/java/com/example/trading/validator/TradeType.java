package com.example.trading.validator;

/** 売買区分を2種類に限定する。 */
public enum TradeType {
    BUY,
    SELL;

    public static TradeType fromInput(String input) {
        if ("BUY".equals(input)) {
            return BUY;
        }
        if ("SELL".equals(input)) {
            return SELL;
        }
        IllegalArgumentException error = new IllegalArgumentException("売買区分はBUY、SELLを入力");

        throw error;
    }
}
