package com.example.trading.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockCodeValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {"7203", "130A", "9A76", "9A7A", "0000"})
    void acceptsSpecifiedFormats(String input) {
        assertEquals(input, StockCodeValidator.validate(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "1B00", "130a", "A300", "13A0", "12345", "123",
            " 7203", "7203 ", "７２０３", "1 00", "130\n"
    })
    void rejectsInvalidCodesWithoutNormalizingInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> StockCodeValidator.validate(input));
    }

    @Test
    void acceptsAllNineteenAllowedLettersAtSecondAndFourthPositions() {
        for (char letter : "ACDFGHJKLMNPRSTUWXY".toCharArray()) {
            String second = "1" + letter + "00";
            String fourth = "130" + letter;
            assertEquals(second, StockCodeValidator.validate(second));
            assertEquals(fourth, StockCodeValidator.validate(fourth));
        }
    }

    @Test
    void rejectsAllSevenDisallowedLettersAtSecondAndFourthPositions() {
        for (char letter : "BEIOQVZ".toCharArray()) {
            assertThrows(IllegalArgumentException.class,
                    () -> StockCodeValidator.validate("1" + letter + "00"));
            assertThrows(IllegalArgumentException.class,
                    () -> StockCodeValidator.validate("130" + letter));
        }
    }

    @Test
    void rejectsEveryLowercaseLetterAndEveryLetterAtDigitOnlyPositions() {
        for (char letter = 'a'; letter <= 'z'; letter++) {
            String second = "1" + letter + "00";
            String fourth = "130" + letter;
            assertThrows(IllegalArgumentException.class, () -> StockCodeValidator.validate(second));
            assertThrows(IllegalArgumentException.class, () -> StockCodeValidator.validate(fourth));
        }
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            String first = letter + "300";
            String third = "13" + letter + "0";
            assertThrows(IllegalArgumentException.class, () -> StockCodeValidator.validate(first));
            assertThrows(IllegalArgumentException.class, () -> StockCodeValidator.validate(third));
        }
    }
}
