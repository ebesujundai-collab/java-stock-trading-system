package com.example.trading.ui;

import com.example.trading.repository.Trade;
import com.example.trading.validator.TradeType;
import com.example.trading.repository.TradeHistoryRepository;
import com.example.trading.validator.TradingHoursService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleApplicationTest {
    // UTCの01:00は日本の平日10:00。PCの時刻やタイムゾーンには依存しない。
    private static final Clock OPEN_CLOCK =
            Clock.fixed(Instant.parse("2026-10-07T01:00:00Z"), ZoneOffset.UTC);

    @TempDir
    Path temporaryDirectory;

    @Test
    void invalidMenuAndTradeFieldsCanBeCorrectedWithoutRestartingTheTrade() throws IOException {
        Path csv = temporaryDirectory.resolve("trades.csv");
        TradeHistoryRepository repository = new TradeHistoryRepository(csv);
        String input = String.join("\n", "unknown", "1", "1B00", "130a", "7203",
                "buy", "HOLD", "BUY", "abc", "0", "-1", "1.5", "100", "0") + "\n";

        String output = runApplication(input, repository, OPEN_CLOCK);

        List<Trade> saved = repository.findAll();
        assertEquals(1, saved.size());
        assertEquals("7203", saved.get(0).getStockCode());
        assertEquals(TradeType.BUY, saved.get(0).getTradeType());
        assertEquals(BigInteger.valueOf(100), saved.get(0).getQuantity());
        assertEquals(9, output.split("入力エラー:", -1).length - 1);
        assertTrue(output.contains("取引が成立しました。CSVに保存しました。"));
        assertTrue(output.contains("終了します。"));
    }

    @Test
    void sellCanBeSavedWithoutCheckingHoldings() throws IOException {
        TradeHistoryRepository repository = repository();

        String output = runApplication("1\n130A\nSELL\n25\n0\n", repository, OPEN_CLOCK);

        assertTrue(output.contains("取引が成立しました。"));
        assertEquals(TradeType.SELL, repository.findAll().get(0).getTradeType());
        assertEquals(BigInteger.valueOf(25), repository.findAll().get(0).getQuantity());
    }

    @Test
    void successfulTradesCanBeViewedAndReloadedAfterRestart() throws IOException {
        Path csv = temporaryDirectory.resolve("trades.csv");
        TradeHistoryRepository repository = new TradeHistoryRepository(csv);

        String firstRun = runApplication(
                "1\n7203\nBUY\n100\n1\n130A\nSELL\n25\n2\nall\n0\n",
                repository, OPEN_CLOCK);

        assertEquals(2, firstRun.split("取引が成立しました。", -1).length - 1);
        assertTrue(firstRun.contains("| 2026-10-07 10:00:00"));
        assertEquals(List.of("tradeDateTime,stockCode,side,quantity",
                "2026-10-07 10:00:00,7203,BUY,100",
                "2026-10-07 10:00:00,130A,SELL,25"), Files.readAllLines(csv));
        String savedCsv = Files.readString(csv);

        // 新しいアプリとrepositoryを作り、メモリーではなく保存ファイルから復元する。
        String secondRun = runApplication("2\ntoday\n2\nyesterday\n0\n",
                new TradeHistoryRepository(csv), OPEN_CLOCK);

        assertTrue(secondRun.contains("7203"));
        assertTrue(secondRun.contains("130A"));
        assertTrue(secondRun.contains("該当する取引履歴はありません。"));
        assertFalse(secondRun.contains("取引が成立しました。"));
        assertEquals(savedCsv, Files.readString(csv));
    }

    @Test
    void allHistoryRetriesInvalidPeriodAndDisplaysNewestFirst() throws IOException {
        TradeHistoryRepository repository = repositoryWithHistory();

        String output = runApplication("2\nALL\nlastweek\nall\n0\n", repository, OPEN_CLOCK);

        assertEquals(2, output.split("入力エラー:", -1).length - 1);
        assertTrue(output.contains("銘柄コード"));
        assertTrue(output.indexOf("7203") < output.indexOf("130A"));
        assertTrue(output.indexOf("130A") < output.indexOf("9A76"));
        // 画面で並べ替えても、CSVの追記順序は変わらない。
        assertEquals("9A76", repository.findAll().get(0).getStockCode());
    }

    @Test
    void todayHistoryDisplaysOnlyJapaneseToday() throws IOException {
        String output = runApplication("2\ntoday\n0\n", repositoryWithHistory(), OPEN_CLOCK);

        assertTrue(output.contains("7203"));
        assertFalse(output.contains("130A"));
        assertFalse(output.contains("9A76"));
    }

    @Test
    void yesterdayHistoryDisplaysOnlyJapaneseYesterday() throws IOException {
        String output = runApplication("2\nyesterday\n0\n", repositoryWithHistory(), OPEN_CLOCK);

        assertTrue(output.contains("130A"));
        assertFalse(output.contains("7203"));
        assertFalse(output.contains("9A76"));
    }

    @Test
    void missingHistoryShowsEmptyMessageWithoutCreatingCsv() {
        Path csv = temporaryDirectory.resolve("trades.csv");

        String output = runApplication("2\nall\n0\n", new TradeHistoryRepository(csv), OPEN_CLOCK);

        assertTrue(output.contains("該当する取引履歴はありません。"));
        assertFalse(Files.exists(csv));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-10-07T02:30:01Z", "2026-10-10T01:00:00Z"})
    void closedSessionAndWeekendRejectTradeAndReturnToMenu(String instant) {
        Path csv = temporaryDirectory.resolve("trades.csv");
        Clock closedClock = Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);

        String output = runApplication("1\n7203\nBUY\n100\n2\nall\n0\n",
                new TradeHistoryRepository(csv), closedClock);

        assertTrue(output.contains("取引できません:"));
        assertTrue(output.contains("該当する取引履歴はありません。"));
        assertFalse(output.contains("取引が成立しました。"));
        assertFalse(Files.exists(csv));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1\n", "1\n7203\n", "1\n7203\nBUY\n", "2\n",
            "unknown\n", "1\n1B00\n", "1\n7203\nbuy\n", "1\n7203\nBUY\n0\n",
            "2\ninvalid\n"})
    void endOfInputAtAnyPromptExitsWithoutSavingPartialTrade(String input) {
        Path csv = temporaryDirectory.resolve("trades.csv");

        String output = runApplication(input, new TradeHistoryRepository(csv), OPEN_CLOCK);

        assertTrue(output.contains("終了します。"));
        assertFalse(output.contains("取引が成立しました。"));
        assertFalse(Files.exists(csv));
    }

    @Test
    void saveAndReadFailuresAreReportedAndMenuRemainsAvailable() throws IOException {
        Path directoryInsteadOfCsv = Files.createDirectory(temporaryDirectory.resolve("trades.csv"));

        String output = runApplication("1\n7203\nBUY\n100\n2\nall\n0\n",
                new TradeHistoryRepository(directoryInsteadOfCsv), OPEN_CLOCK);

        assertTrue(output.contains("取引を保存できませんでした:"));
        assertTrue(output.contains("履歴を読み込めませんでした:"));
        assertTrue(output.contains("終了します。"));
        assertFalse(output.contains("取引が成立しました。"));
        assertTrue(Files.isDirectory(directoryInsteadOfCsv));
    }

    @Test
    void corruptHistoryIsReportedWithoutChangingTheFile() throws IOException {
        Path csv = temporaryDirectory.resolve("trades.csv");
        String invalidCsv = "invalid header\n";
        Files.writeString(csv, invalidCsv);

        String output = runApplication("2\nall\n0\n", new TradeHistoryRepository(csv), OPEN_CLOCK);

        assertTrue(output.contains("履歴を読み込めませんでした:"));
        assertTrue(output.contains("終了します。"));
        assertEquals(invalidCsv, Files.readString(csv));
    }

    @Test
    void inputReadFailureExitsWithMessage() {
        BufferedReader brokenInput = new BufferedReader(new StringReader("")) {
            @Override
            public String readLine() throws IOException {
                throw new IOException("テスト用入力エラー");
            }
        };

        String output = runApplication(brokenInput, repository(), OPEN_CLOCK);

        assertTrue(output.contains("入力を読み込めませんでした: テスト用入力エラー"));
        assertTrue(output.contains("終了します。"));
    }

    private TradeHistoryRepository repository() {
        return new TradeHistoryRepository(temporaryDirectory.resolve("trades.csv"));
    }

    private TradeHistoryRepository repositoryWithHistory() throws IOException {
        TradeHistoryRepository repository = repository();
        repository.append(trade("2026-10-05T10:00:00", "9A76"));
        repository.append(trade("2026-10-07T09:15:00", "7203"));
        repository.append(trade("2026-10-06T15:00:00", "130A"));
        return repository;
    }

    private Trade trade(String dateTime, String stockCode) {
        return new Trade(LocalDateTime.parse(dateTime), stockCode, TradeType.BUY, BigInteger.ONE);
    }

    private String runApplication(String input, TradeHistoryRepository repository, Clock clock) {
        return runApplication(new BufferedReader(new StringReader(input)), repository, clock);
    }

    private String runApplication(BufferedReader input, TradeHistoryRepository repository, Clock clock) {
        StringWriter result = new StringWriter();
        PrintWriter output = new PrintWriter(result, true);
        TradingService tradingService = new TradingService(repository, new TradingHoursService(), clock);
        TradeHistoryService historyService = new TradeHistoryService(repository, clock);
        ConsoleApplication application = new ConsoleApplication(input, output, tradingService,
                historyService, new TradeHistoryTablePrinter(output));

        application.run();
        return result.toString();
    }
}
