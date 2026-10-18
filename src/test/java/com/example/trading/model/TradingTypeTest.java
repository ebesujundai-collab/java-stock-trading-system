package com.example.trading.model;

import com.example.trading.validator.TradeType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TradingTypeTest {
    @ParameterizedTest
    @CsvSource({"BUY, BUY", "SELL, SELL"})
    void acceptsOnlyDefinedTradeTypes(String input, TradeType expected) {
        assertEquals(expected, TradeType.fromInput(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"buy", "sell", "Buy", "BUY ", " SELL", "HOLD", "1"})
    void rejectsOtherInputs(String input) {
        assertThrows(IllegalArgumentException.class, () -> TradeType.fromInput(input));
    }
}
