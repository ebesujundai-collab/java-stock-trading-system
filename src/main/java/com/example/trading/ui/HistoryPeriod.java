package com.example.trading.ui;

/** 履歴表示で選択できる期間。入力は小文字、コード内ではenum定数を使う。 */
public enum HistoryPeriod {
    ALL,
    TODAY,
    YESTERDAY;

    public static HistoryPeriod fromInput(String input) {
        switch (input) {
            case "", "all":
                return ALL;
            case "today":
                return TODAY;
            case "yesterday":
                return YESTERDAY;
            default:
                IllegalArgumentException exception = new IllegalArgumentException("all、today、yesterdayのいずれか");

                throw exception;
        }
    }
}
