//ここからStep3

package com.example.trading.validator;

public enum HistoryType {
    ALL,
    TODAY,
    YESTERDAY;

    public static HistoryType fromInput(String input) {

        if ("all".equals(input)) {
            return ALL;
        }

        if ("today".equals(input)) {
            return TODAY;
        }

        if ("yesterday".equals(input)) {
            return YESTERDAY;
        }

        throw new IllegalArgumentException(
                "all、today、yesterdayを入力"
        );
    }
}