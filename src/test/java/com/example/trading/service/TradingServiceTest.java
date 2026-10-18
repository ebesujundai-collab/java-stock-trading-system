package com.example.trading.service;

import com.example.trading.model.Trade;
import com.example.trading.model.TradeType;
import com.example.trading.repository.TradingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TradingServiceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void savesSuccessfulTradeUsingJapaneseTimeAndSecondPrecision() throws IOException {
        Path csv = temporaryDirectory.resolve("history.csv");
        TradingRepository repository = new TradingRepository(csv);
        // UTCでは00時でも、日本時間では09時なので成立する。
        Clock clock = Clock.fixed(Instant.parse("2026-10-07T00:15:32.123456789Z"), ZoneOffset.UTC);
        TradingService service = new TradingService(repository, new TradingHoursService(), clock);

        Trade trade = service.execute("7203", TradeType.BUY, BigInteger.valueOf(100));

        assertEquals(LocalDateTime.of(2026, 10, 7, 9, 15, 32), trade.getTradeDateTime());
        assertEquals("7203", trade.getStockCode());
        assertEquals(TradeType.BUY, trade.getTradeType());
        assertEquals(BigInteger.valueOf(100), trade.getQuantity());
        List<Trade> saved = repository.findAll();
        assertEquals(1, saved.size());
        assertEquals(trade.getTradeDateTime(), saved.getFirst().getTradeDateTime());
        assertEquals("7203", saved.getFirst().getStockCode());
        assertEquals(TradeType.BUY, saved.getFirst().getTradeType());
        assertEquals(BigInteger.valueOf(100), saved.getFirst().getQuantity());
    }

    @Test
    void sellsWithoutHoldingChecksAndAllowsAnyPositiveWholeQuantity() throws IOException {
        Path csv = temporaryDirectory.resolve("history.csv");
        TradingService service = serviceAt(csv, "2026-10-07T09:00:00");
        String hugeQuantity = "9999999999999999999999999999999999999999";

        Trade trade = service.execute("9A7A", TradeType.SELL, new BigInteger(hugeQuantity));

        assertEquals(TradeType.SELL, trade.getTradeType());
        assertEquals(new BigInteger(hugeQuantity), trade.getQuantity());
        assertEquals(1, new TradingRepository(csv).findAll().size());
    }

    @ParameterizedTest
    @CsvSource({"1B00, BUY, 100", "7203, HOLD, 100", "7203, buy, 100",
            "7203, BUY, 0", "7203, BUY, -1", "7203, BUY, abc", "7203, BUY, 1.5"})
    void invalidInputDoesNotCreateHistory(String stockCode, String side, String quantity) {
        Path csv = temporaryDirectory.resolve("history.csv");
        TradingService service = serviceAt(csv, "2026-10-07T09:00:00");

        assertThrows(IllegalArgumentException.class, () -> service.execute(stockCode, TradeType.valueOf(side), new BigInteger(quantity)));

        assertFalse(Files.exists(csv));
    }

    @Test
    void invalidTradeDoesNotModifyAnExistingHistory() throws IOException {
        Path csv = temporaryDirectory.resolve("history.csv");
        TradingService service = serviceAt(csv, "2026-10-07T09:00:00");
        service.execute("7203", TradeType.BUY, BigInteger.ONE);
        String before = Files.readString(csv);

        assertThrows(IllegalArgumentException.class, () -> service.execute("7203", TradeType.SELL, BigInteger.ZERO));

        assertEquals(before, Files.readString(csv));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-10-07T08:59:59", "2026-10-07T11:30:00.000000001",
            "2026-10-07T12:29:59", "2026-10-07T15:30:00.000000001",
            "2026-10-10T09:00:00", "2026-10-11T09:00:00"})
    void closedSessionsDoNotCreateHistory(String japaneseDateTime) {
        Path csv = temporaryDirectory.resolve("history.csv");
        TradingService service = serviceAt(csv, japaneseDateTime);

        assertThrows(IllegalArgumentException.class, () -> service.execute("7203", TradeType.BUY, BigInteger.ONE));

        assertFalse(Files.exists(csv));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-10-07T09:00:00", "2026-10-07T11:30:00",
            "2026-10-07T12:30:00", "2026-10-07T15:30:00", "2026-01-01T09:00:00"})
    void acceptsInclusiveSessionBoundariesAndWeekdayHolidays(String japaneseDateTime)
            throws IOException {
        Path csv = temporaryDirectory.resolve("history.csv");
        Trade trade = serviceAt(csv, japaneseDateTime).execute("130A", "BUY", "1");
        assertEquals(LocalDateTime.parse(japaneseDateTime), trade.getTradeDateTime());
        assertEquals(1, new TradingRepository(csv).findAll().size());
    }

    @Test
    void propagatesSavingFailureInsteadOfReturningASuccessfulTrade() throws IOException {
        Path directoryInsteadOfCsv = Files.createDirectory(temporaryDirectory.resolve("history.csv"));
        TradingService service = serviceAt(directoryInsteadOfCsv, "2026-10-07T09:00:00");

        assertThrows(IOException.class, () -> service.execute("7203", TradeType.BUY, BigInteger.ONE));
    }

    @Test
    void readsTheClockOnceSoValidationAndSavedDateTimeUseTheSameInstant() throws IOException {
        Path csv = temporaryDirectory.resolve("history.csv");
        CountingClock clock = new CountingClock(Instant.parse("2026-10-07T02:30:00Z"));
        TradingService service = new TradingService(
                new TradingRepository(csv), new TradingHoursService(), clock);

        Trade trade = service.execute("7203", TradeType.BUY, BigInteger.ONE);

        assertEquals(1, clock.readCount);
        assertEquals(LocalDateTime.of(2026, 10, 7, 11, 30), trade.getTradeDateTime());
    }

    private TradingService serviceAt(Path csv, String japaneseDateTime) {
        Instant instant = LocalDateTime.parse(japaneseDateTime)
                .atZone(TradingHoursService.JAPAN_ZONE).toInstant();
        Clock clock = Clock.fixed(instant, ZoneOffset.UTC);
        return new TradingService(new TradingRepository(csv), new TradingHoursService(), clock);
    }

    /** 読み取るたびに1秒進む時計で、境界時刻の二重取得を検出する。 */
    private static final class CountingClock extends Clock {
        private final Instant firstInstant;
        private int readCount;

        private CountingClock(Instant firstInstant) {
            this.firstInstant = firstInstant;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.fixed(firstInstant, zone);
        }

        @Override
        public Instant instant() {
            Instant value = firstInstant.plusSeconds(readCount);
            readCount++;
            return value;
        }
    }
}
