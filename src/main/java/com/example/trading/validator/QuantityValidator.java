package com.example.trading.validator;

import java.math.BigInteger;
import java.util.regex.Pattern;

/** 株数の文字列を、1以上の整数へ変換する。 */
public final class QuantityValidator {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("[0-9]+");
    private static final String ERROR_MESSAGE = "株数は1以上の整数を入力してください。";

    private QuantityValidator() {
    }

    public static BigInteger parse(String input) {
        if (input == null || !INTEGER_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        // BigIntegerを使い、intの上限による株数制限を設けない。
        BigInteger quantity = new BigInteger(input);
        if (quantity.signum() <= 0) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        return quantity;
    }
}
