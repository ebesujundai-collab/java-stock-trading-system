package com.example.trading.validator;

import java.util.regex.Pattern;

/** 銘柄の実在確認は行わず、4文字の形式だけを検証する。 */
public final class StockCodeValidator {
    private static final String ALLOWED_LETTERS = "ACDFGHJKLMNPRSTUWXY";
    private static final Pattern STOCK_CODE_PATTERN = Pattern.compile(
            "[0-9][0-9" + ALLOWED_LETTERS + "][0-9][0-9" + ALLOWED_LETTERS + "]");

    private StockCodeValidator() {
        // 状態を持たない検証処理なので、インスタンスを作らずstaticメソッドを使う
        }

    public static String validate(String stockCode) {
        if (stockCode == null || !STOCK_CODE_PATTERN.matcher(stockCode).matches()) {
            throw new IllegalArgumentException("銘柄コードは4文字で、1・3文字目は数字、2・4文字目は数字または" + "指定の英大文字（" + ALLOWED_LETTERS + "）を入力してください。");
        }
        return stockCode;
    }
}
