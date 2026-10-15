package com.example.trading.ui;

import com.example.trading.repository.Trade;
import com.example.trading.validator.TradeType;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeHistoryTablePrinterTest {
    @Test
    void displaysJapaneseHeadersBordersAndTradeValuesInGivenOrder() {
        Trade newer = trade("2026-10-07T15:20:14", "7203", TradeType.BUY, "100");
        Trade older = trade("2026-10-07T14:31:42", "130A", TradeType.SELL, "25");
        List<Trade> trades = new ArrayList<>(List.of(newer, older));

        String output = print(trades);

        assertTrue(output.startsWith("+"));
        assertTrue(output.contains("取引日時"));
        assertTrue(output.contains("銘柄コード"));
        assertTrue(output.contains("売買"));
        assertTrue(output.contains("株数"));
        assertTrue(output.contains("2026-10-07 15:20:14"));
        assertTrue(output.contains("2026-10-07 14:31:42"));
        assertTrue(output.contains("BUY"));
        assertTrue(output.contains("SELL"));
        assertTrue(output.contains("100"));
        assertTrue(output.contains("25"));
        assertTrue(output.indexOf("7203") < output.indexOf("130A"));
        assertEquals(List.of(newer, older), trades);
        assertEquals(6, output.lines().count());
    }

    @Test
    void emptyHistoryShowsHelpfulMessage() {
        String output = print(List.of());

        assertTrue(output.contains("該当する取引履歴はありません。"));
        assertFalse(output.contains("|"));
    }

    @Test
    void largeQuantityFitsInsideTableAndJapaneseHeadersStayAligned() {
        String quantity = "1234567890123456789012345678901234567890";
        Trade trade = trade("2026-10-07T09:00:00", "9A7A", TradeType.SELL, quantity);

        String output = print(List.of(trade));

        assertTrue(output.contains(quantity));
        List<String> lines = output.lines().toList();
        int expectedWidth = lines.get(0).length();
        for (String line : lines) {
            // 日本語の見出し1文字は、この表では半角2文字分の表示幅になる。
            long japaneseCharacters = line.chars().filter(character -> character > 127).count();
            assertEquals(expectedWidth, line.length() + japaneseCharacters);
        }
    }

    private Trade trade(String dateTime, String stockCode, TradeType side, String quantity) {
        return new Trade(LocalDateTime.parse(dateTime), stockCode, side, new BigInteger(quantity));
    }

    private String print(List<Trade> trades) {
        StringWriter result = new StringWriter();
        TradeHistoryTablePrinter printer = new TradeHistoryTablePrinter(new PrintWriter(result, true));
        printer.print(trades);
        return result.toString();
    }
}
