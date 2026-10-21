package com.example.trading.Step3;


public enum HistoryType {
    ALL,
    TODAY,
    YESTERDAY;

    public static HistoryType fromInput(String input) {

        if ("all".equals(input) || "".equals(input)) {
            return ALL;
        }

        if ("today".equals(input)) {
            return TODAY;
        }

        if ("yesterday".equals(input)) {
            return YESTERDAY;
        }

        IllegalArgumentException error = new IllegalArgumentException("all、today、yesterdayを入力");
        throw error;
    }
}