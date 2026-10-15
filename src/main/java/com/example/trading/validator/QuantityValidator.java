package com.example.trading.validator;

import java.math.BigInteger;

public final class QuantityValidator {

    private static final String ERROR_MESSAGE =
            "株数は1以上の整数を入力してください。";

    private QuantityValidator() {}

    public static BigInteger parse(String input) {

        if (input == null || !input.matches("[0-9]+")) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }

        BigInteger quantity = new BigInteger(input);

        if (quantity.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }

        return quantity;
    }
}