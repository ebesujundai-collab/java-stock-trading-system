package com.example.trading.service;

import com.example.trading.ui.HistoryPeriod;
import com.example.trading.repository.Trade;
import com.example.trading.ui.TradeHistoryService;
import com.example.trading.validator.TradeType;
import com.example.trading.repository.TradeHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeHistoryServiceTest {
    @TempDir
    Path directory;

    private Path file;
    private TradeHistoryRepository repository;
    private TradeHistoryService service;

    @BeforeEach
    void setUp() {
        file = directory.resolve("com/example/trading/csv/trade_history.csv");
        repository = new TradeHistoryRepository(file);
        // UTCでは10月6日、東京では10月7日00:30。時計のzoneにも依存させない。
        Clock clock = Clock.fixed(Instant.parse("2026-10-06T15:30:00Z"), ZoneOffset.UTC);
        service = new TradeHistoryService(repository, clock);
    }

    @Test
    void allIncludesEveryDateAndSortsByDescendingDateTime() throws IOException {
        append("2026-10-06T10:00:00", "1000");
        append("2026-10-07T09:00:00", "2000");
        append("2026-10-05T15:00:00", "3000");
        append("2026-10-07T15:00:00", "4000");

        List<Trade> result = service.findByPeriod(HistoryPeriod.ALL);

        assertEquals(List.of("4000", "2000", "1000", "3000"), stockCodes(result));
    }

    @Test
    void todayIncludesOnlyTokyoTodayIncludingBothDateBoundaries() throws IOException {
        append("2026-10-06T23:59:59", "1000");
        append("2026-10-07T00:00:00", "2000");
        append("2026-10-07T23:59:59", "3000");
        append("2026-10-08T00:00:00", "4000");

        List<Trade> result = service.findByPeriod(HistoryPeriod.TODAY);

        assertEquals(List.of("3000", "2000"), stockCodes(result));
    }

    @Test
    void yesterdayIncludesOnlyTokyoYesterdayIncludingBothDateBoundaries() throws IOException {
        append("2026-10-05T23:59:59", "1000");
        append("2026-10-06T00:00:00", "2000");
        append("2026-10-06T23:59:59", "3000");
        append("2026-10-07T00:00:00", "4000");

        List<Trade> result = service.findByPeriod(HistoryPeriod.YESTERDAY);

        assertEquals(List.of("3000", "2000"), stockCodes(result));
    }

    @ParameterizedTest
    @CsvSource({
            "2025-12-31T15:00:00Z, 2025-12-31T09:00:00, 2026-01-01T09:00:00",
            "2026-02-28T15:00:00Z, 2026-02-28T09:00:00, 2026-03-01T09:00:00",
            "2028-02-29T15:00:00Z, 2028-02-29T09:00:00, 2028-03-01T09:00:00"
    })
    void filtersAcrossYearMonthAndLeapDayBoundaries(String now, String yesterday, String today)
            throws IOException {
        service = new TradeHistoryService(repository,
                Clock.fixed(Instant.parse(now), ZoneOffset.UTC));
        append(yesterday, "1000");
        append(today, "2000");

        assertEquals(List.of("1000"), stockCodes(service.findByPeriod(HistoryPeriod.YESTERDAY)));
        assertEquals(List.of("2000"), stockCodes(service.findByPeriod(HistoryPeriod.TODAY)));
    }

    @Test
    void yesterdayMeansPreviousCalendarDayEvenOnMonday() throws IOException {
        service = new TradeHistoryService(repository,
                Clock.fixed(Instant.parse("2026-10-11T15:00:00Z"), ZoneOffset.UTC));
        append("2026-10-09T09:00:00", "1000");
        append("2026-10-11T09:00:00", "2000");
        append("2026-10-12T09:00:00", "3000");

        assertEquals(List.of("2000"), stockCodes(service.findByPeriod(HistoryPeriod.YESTERDAY)));
    }

    @Test
    void equalTimestampsKeepCsvOrderAndSortingDoesNotRewriteCsv() throws IOException {
        append("2026-10-06T09:00:00", "1000");
        append("2026-10-07T09:00:00", "2000");
        append("2026-10-07T09:00:00", "3000");
        append("2026-10-05T09:00:00", "4000");
        byte[] before = Files.readAllBytes(file);

        List<Trade> result = service.findByPeriod(HistoryPeriod.ALL);

        assertEquals(List.of("2000", "3000", "1000", "4000"), stockCodes(result));
        assertArrayEquals(before, Files.readAllBytes(file));
        assertEquals(List.of("1000", "2000", "3000", "4000"), stockCodes(repository.findAll()));
    }

    @ParameterizedTest
    @EnumSource(HistoryPeriod.class)
    void missingFileReturnsEmptyHistoryForEveryPeriod(HistoryPeriod period) throws IOException {
        assertTrue(service.findByPeriod(period).isEmpty());
    }

    @Test
    void periodWithoutMatchingTradesReturnsEmptyHistory() throws IOException {
        append("2026-10-05T09:00:00", "1000");

        assertTrue(service.findByPeriod(HistoryPeriod.TODAY).isEmpty());
        assertTrue(service.findByPeriod(HistoryPeriod.YESTERDAY).isEmpty());
    }

    @Test
    void repositoryReadErrorIsReportedInsteadOfPretendingHistoryIsEmpty() throws IOException {
        Files.writeString(file, "broken header\n");

        IOException exception = assertThrows(IOException.class,
                () -> service.findByPeriod(HistoryPeriod.ALL));

        assertTrue(exception.getMessage().contains("1行目"));
    }

    private void append(String dateTime, String stockCode) throws IOException {
        repository.append(new Trade(LocalDateTime.parse(dateTime), stockCode,
                TradeType.BUY, BigInteger.ONE));
    }

    private List<String> stockCodes(List<Trade> trades) {
        return trades.stream().map(Trade::getStockCode).toList();
    }
}
