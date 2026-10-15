package com.example.trading.service;

import com.example.trading.model.HistoryPeriod;
import com.example.trading.model.Trade;
import com.example.trading.model.TradeType;
import com.example.trading.repository.TradeHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeHistoryServiceTest {

    private static final String HEADER =
            "tradeDateTime,stockCode,side,quantity";

    @TempDir
    Path directory;

    private Path file;
    private TradeHistoryRepository repository;
    private TradeHistoryService service;

    @BeforeEach
    void setUp() throws IOException {
        file = directory.resolve("trade_history.csv");
        Files.writeString(file, HEADER + "\n");

        repository = new TradeHistoryRepository(file);

        Clock clock = Clock.fixed(
                Instant.parse("2026-10-07T01:00:00Z"),
                ZoneOffset.UTC
        );

        service = new TradeHistoryService(repository, clock);
    }

    @Test
    void allReturnsEveryTradeNewestFirst() throws IOException {
        append("2026-10-06T10:00:00", "1111");
        append("2026-10-07T09:00:00", "2222");
        append("2026-10-05T15:00:00", "3333");
        append("2026-10-07T15:00:00", "4444");

        List<Trade> result =
                service.findByPeriod(HistoryPeriod.ALL);

        assertEquals(
                List.of("4444", "2222", "1111", "3333"),
                stockCodes(result)
        );
    }

    @Test
    void todayReturnsOnlyTodaysTrades() throws IOException {
        append("2026-10-06T23:59:59", "1111");
        append("2026-10-07T00:00:00", "2222");
        append("2026-10-07T23:59:59", "3333");
        append("2026-10-08T00:00:00", "4444");

        List<Trade> result =
                service.findByPeriod(HistoryPeriod.TODAY);

        assertEquals(
                List.of("3333", "2222"),
                stockCodes(result)
        );
    }

    @Test
    void yesterdayReturnsOnlyPreviousCalendarDay() throws IOException {
        append("2026-10-05T23:59:59", "1111");
        append("2026-10-06T00:00:00", "2222");
        append("2026-10-06T23:59:59", "3333");
        append("2026-10-07T00:00:00", "4444");

        List<Trade> result =
                service.findByPeriod(HistoryPeriod.YESTERDAY);

        assertEquals(
                List.of("3333", "2222"),
                stockCodes(result)
        );
    }

    @Test
    void equalDateTimesKeepCsvOrder() throws IOException {
        append("2026-10-07T09:00:00", "1111");
        append("2026-10-07T09:00:00", "2222");

        List<Trade> result =
                service.findByPeriod(HistoryPeriod.ALL);

        assertEquals(
                List.of("1111", "2222"),
                stockCodes(result)
        );
    }

    @Test
    void noMatchingTradeReturnsEmptyList() throws IOException {
        append("2026-10-05T09:00:00", "1111");

        assertTrue(
                service.findByPeriod(HistoryPeriod.TODAY).isEmpty()
        );
    }

    private void append(String dateTime, String stockCode)
            throws IOException {

        repository.append(
                new Trade(
                        LocalDateTime.parse(dateTime),
                        stockCode,
                        TradeType.BUY,
                        BigInteger.ONE
                )
        );
    }

    private List<String> stockCodes(List<Trade> trades) {
        return trades.stream()
                .map(Trade::getStockCode)
                .toList();
    }
}
