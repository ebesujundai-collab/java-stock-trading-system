package com.example.trading.repository;

import com.example.trading.model.Trade;
import com.example.trading.model.TradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeHistoryRepositoryTest {

    private static final String HEADER =
            "tradeDateTime,stockCode,side,quantity";

    @TempDir
    Path directory;

    private Path file;
    private TradeHistoryRepository repository;

    @BeforeEach
    void setUp() {
        file = directory.resolve("trade_history.csv");
        repository = new TradeHistoryRepository(file);
    }

    @Test
    void missingFileReturnsEmptyList() throws IOException {
        assertTrue(repository.findAll().isEmpty());
        assertFalse(Files.exists(file));
    }

    @Test
    void appendCreatesFileAndWritesTrade() throws IOException {
        Trade trade = trade(
                "2026-10-07T09:15:32",
                "7203",
                TradeType.BUY,
                "100"
        );

        repository.append(trade);

        assertTrue(Files.exists(file));
        assertEquals(
                "2026-10-07 09:15:32,7203,BUY,100\n",
                Files.readString(file)
        );
    }

    @Test
    void appendAddsTradeBelowExistingHeader() throws IOException {
        Files.writeString(file, HEADER + "\n");

        repository.append(trade(
                "2026-10-07T09:15:32",
                "7203",
                TradeType.BUY,
                "100"
        ));

        assertEquals(
                List.of(
                        HEADER,
                        "2026-10-07 09:15:32,7203,BUY,100"
                ),
                Files.readAllLines(file)
        );
    }

    @Test
    void findAllReadsCsvRowsAsTradeObjects() throws IOException {
        Files.writeString(
                file,
                HEADER + "\n"
                        + "2026-10-07 09:15:32,7203,BUY,100\n"
                        + "2026-10-07 10:25:18,130A,SELL,25\n"
        );

        List<Trade> trades = repository.findAll();

        assertEquals(2, trades.size());

        Trade first = trades.get(0);
        assertEquals(LocalDateTime.of(2026, 10, 7, 9, 15, 32),
                first.getTradeDateTime());
        assertEquals("7203", first.getStockCode());
        assertEquals(TradeType.BUY, first.getTradeType());
        assertEquals(BigInteger.valueOf(100), first.getQuantity());

        Trade second = trades.get(1);
        assertEquals("130A", second.getStockCode());
        assertEquals(TradeType.SELL, second.getTradeType());
        assertEquals(BigInteger.valueOf(25), second.getQuantity());
    }

    @Test
    void findAllKeepsCsvOrder() throws IOException {
        Files.writeString(file, HEADER + "\n");

        repository.append(trade(
                "2026-10-07T10:00:00",
                "1111",
                TradeType.BUY,
                "10"
        ));

        repository.append(trade(
                "2026-10-07T11:00:00",
                "2222",
                TradeType.SELL,
                "20"
        ));

        List<Trade> trades = repository.findAll();

        assertEquals("1111", trades.get(0).getStockCode());
        assertEquals("2222", trades.get(1).getStockCode());
    }

    @Test
    void headerOnlyFileReturnsEmptyList() throws IOException {
        Files.writeString(file, HEADER + "\n");

        assertTrue(repository.findAll().isEmpty());
    }

    private Trade trade(
            String dateTime,
            String stockCode,
            TradeType tradeType,
            String quantity
    ) {
        return new Trade(
                LocalDateTime.parse(dateTime),
                stockCode,
                tradeType,
                new BigInteger(quantity)
        );
    }
}
