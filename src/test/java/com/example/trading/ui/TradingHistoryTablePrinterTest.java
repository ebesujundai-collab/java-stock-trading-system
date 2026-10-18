package com.example.trading.ui;

import com.example.trading.repository.Trading;
import com.example.trading.validator.TradeType;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradingHistoryTablePrinterTest {

    @Test
    void printsHeaderAndTradeRowsInGivenOrder() {
        Trading first = trade(
                "2026-10-07T15:20:14",
                "7203",
                TradeType.BUY,
                "100"
        );

        Trading second = trade(
                "2026-10-07T14:31:42",
                "130A",
                TradeType.SELL,
                "25"
        );

        String output = print(List.of(first, second));

        assertTrue(output.contains(
                "| tradeDateTime       | stockCode | side | quantity |"
        ));

        assertTrue(output.contains(
                "| 2026-10-07 15:20:14 | 7203      | BUY  | 100      |"
        ));

        assertTrue(output.contains(
                "| 2026-10-07 14:31:42 | 130A      | SELL | 25       |"
        ));

        assertTrue(
                output.indexOf("7203") < output.indexOf("130A")
        );
    }

    @Test
    void emptyHistoryPrintsMessageOnly() {
        String output = print(List.of());

        assertTrue(
                output.contains("該当する取引履歴はありません。")
        );
        assertFalse(output.contains("|"));
    }

    @Test
    void normalRowsHaveSameLengthAsHeader() {
        Trading trading = trade(
                "2026-10-07T09:00:00",
                "9A7A",
                TradeType.SELL,
                "100"
        );

        List<String> lines =
                print(List.of(trading)).lines().toList();

        assertEquals(lines.get(1).length(), lines.get(2).length());
    }

    @Test
    void largeQuantityIsStillDisplayed() {
        String quantity =
                "123456789012345678901234567890";

        String output = print(
                List.of(
                        trade(
                                "2026-10-07T09:00:00",
                                "9A7A",
                                TradeType.SELL,
                                quantity
                        )
                )
        );

        assertTrue(output.contains(quantity));
    }

    private Trading trade(
            String dateTime,
            String stockCode,
            TradeType side,
            String quantity
    ) {
        return new Trading(
                LocalDateTime.parse(dateTime),
                stockCode,
                side,
                new BigInteger(quantity)
        );
    }

    private String print(List<Trading> tradings) {
        StringWriter result = new StringWriter();

        TradeHistoryTablePrinter printer =
                new TradeHistoryTablePrinter(
                        new PrintWriter(result, true)
                );

        printer.print(tradings);

        return result.toString();
    }
}
