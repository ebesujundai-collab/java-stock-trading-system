package com.example.trading.model;

/** 履歴表示で選択できる期間。入力は小文字、コード内ではenum定数を使う。 */
public enum HistoryPeriod {
    ALL,
    TODAY,
    YESTERDAY;

    public static HistoryPeriod fromInput(String input) {
        if (input == null) {
            throw invalidInput();
        }
        return switch (input) {
            case "all" -> ALL;
            case "today" -> TODAY;
            case "yesterday" -> YESTERDAY;
            default -> throw invalidInput();
        };
    }

    private static IllegalArgumentException invalidInput() {
        return new IllegalArgumentException(
                "履歴の期間はall、today、yesterdayのいずれかを入力してください。");
    }
}
