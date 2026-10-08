package com.example.trading.validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuantityValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {"1", "10", "100", "150", "1000", "0001",
            "999999999999999999999999999999999999999999999999999999"})
    void acceptsPositiveWholeNumbersWithoutAnIntLimit(String input) {
        assertEquals(new BigInteger(input), QuantityValidator.parse(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"0", "000", "-1", "-100", "1.5", "1.0", "abc",
            "+1", "1e3", " 1", "1 ", "１", "1\n"})
    void rejectsNonPositiveOrNonIntegerInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> QuantityValidator.parse(input));
    }
}
