package com.example.trading.repository;

import com.example.trading.validator.TradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeHistoryRepositoryTest {
    private static final String HEADER = "tradeDateTime,stockCode,side,quantity";
    private static final String FIRST_ROW = "2026-10-07 09:15:32,7203,BUY,100";
    private static final String SECOND_ROW = "2026-10-07 10:25:18,130A,SELL,25";

    @TempDir
    Path directory;

    private Path file;
    private TradeHistoryRepository repository;

    @BeforeEach
    void setUp() {
        file = directory.resolve("com/example/trading/csv/trade_history.csv");
        repository = new TradeHistoryRepository(file);
    }

    @Test
    void missingFileReturnsEmptyHistoryWithoutCreatingFile() throws IOException {
        assertTrue(repository.findAll().isEmpty());
        assertFalse(Files.exists(file));
    }

    @Test
    void savesHeaderAndAllFourFieldsAndLoadsThemAgain() throws IOException {
        repository.append(firstTrade());

        assertEquals(HEADER + "\n" + FIRST_ROW + "\n", Files.readString(file));
        List<Trade> trades = repository.findAll();
        assertEquals(1, trades.size());
        assertTrade(firstTrade(), trades.getFirst());
    }

    @Test
    void appendsInInsertionOrderWithoutAddingAnotherHeader() throws IOException {
        repository.append(secondTrade());
        repository.append(firstTrade());

        assertEquals(HEADER + "\n" + SECOND_ROW + "\n" + FIRST_ROW + "\n",
                Files.readString(file));
        List<Trade> trades = repository.findAll();
        assertTrade(secondTrade(), trades.get(0));
        assertTrade(firstTrade(), trades.get(1));
    }

    @Test
    void emptyExistingFileGetsOneHeaderOnFirstAppend() throws IOException {
        Files.createFile(file);
        assertTrue(repository.findAll().isEmpty());

        repository.append(firstTrade());
        repository.append(secondTrade());

        assertEquals(List.of(HEADER, FIRST_ROW, SECOND_ROW), Files.readAllLines(file));
    }

    @Test
    void headerOnlyFileHasNoTrades() throws IOException {
        Files.writeString(file, HEADER + "\n");
        assertTrue(repository.findAll().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "\n", "\r\n", "\r"})
    void appendsSeparateRowWithOrWithoutFinalLineBreak(String ending) throws IOException {
        Files.writeString(file, HEADER + "\n" + FIRST_ROW + ending);

        repository.append(secondTrade());

        assertEquals(List.of(HEADER, FIRST_ROW, SECOND_ROW), Files.readAllLines(file));
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void appendsAfterHeaderWithoutFinalLineBreak() throws IOException {
        Files.writeString(file, HEADER);

        repository.append(firstTrade());

        assertEquals(HEADER + "\n" + FIRST_ROW + "\n", Files.readString(file));
    }

    @Test
    void roundTripsQuantityLargerThanLong() throws IOException {
        BigInteger quantity = new BigInteger("999999999999999999999999999999999999999999999");
        Trade trade = new Trade(LocalDateTime.of(2026, 10, 7, 9, 0),
                "9A7A", TradeType.SELL, quantity);

        repository.append(trade);

        assertTrade(trade, repository.findAll().getFirst());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "stockCode,tradeDateTime,side,quantity",
            "tradeDateTime,stockCode,side",
            "tradeDateTime,stockCode,side,quantity ",
            "2026-10-07 09:15:32,7203,BUY,100",
            "\uFEFFtradeDateTime,stockCode,side,quantity"
    })
    void rejectsInvalidHeaderWithLineNumber(String header) throws IOException {
        Files.writeString(file, header + "\n" + FIRST_ROW + "\n");

        IOException exception = assertThrows(IOException.class, repository::findAll);

        assertTrue(exception.getMessage().contains("1行目"));
        assertTrue(exception.getMessage().contains("ヘッダー"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-10-07 09:15:32,7203,BUY",
            "2026-10-07 09:15:32,7203,BUY,100,extra",
            "2026-10-07 09:15:32,7203,BUY,",
            "2026-10-07 09:15:32,1B00,BUY,100",
            "2026-10-07 09:15:32,7203,HOLD,100",
            "2026-10-07 09:15:32,7203,buy,100",
            "2026-10-07 09:15:32,7203,BUY,0",
            "2026-10-07 09:15:32,7203,BUY,-1",
            "2026-10-07 09:15:32,7203,BUY,abc",
            "2026-10-07 09:15:32,7203,BUY,1.5",
            "2026-02-30 09:15:32,7203,BUY,100",
            "2026-02-29 09:15:32,7203,BUY,100",
            "2026-10-07 24:00:00,7203,BUY,100",
            "2026-10-07 09:15:60,7203,BUY,100",
            "2026-10-07T09:15:32,7203,BUY,100",
            "2026-10-07 09:15:32.001,7203,BUY,100",
            "2026-10-07 09:15:32 ,7203,BUY,100",
            "",
            "   "
    })
    void rejectsMalformedRowWithItsActualLineNumber(String row) throws IOException {
        Files.writeString(file, HEADER + "\n" + FIRST_ROW + "\n" + row + "\n");

        IOException exception = assertThrows(IOException.class, repository::findAll);

        assertTrue(exception.getMessage().contains("3行目"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "incorrect header\n",
            HEADER + "\n2026-02-30 09:15:32,7203,BUY,100\n",
            HEADER + "\n" + FIRST_ROW + "\n\n"
    })
    void appendRejectsCorruptExistingFileWithoutChangingIt(String contents) throws IOException {
        Files.writeString(file, contents);
        byte[] before = Files.readAllBytes(file);

        assertThrows(IOException.class, () -> repository.append(secondTrade()));

        assertArrayEquals(before, Files.readAllBytes(file));
    }

    @Test
    void rejectsInvalidUtf8WithoutChangingFile() throws IOException {
        byte[] invalidUtf8 = {(byte) 0xC3, (byte) 0x28};
        Files.write(file, invalidUtf8);

        assertThrows(IOException.class, repository::findAll);
        assertThrows(IOException.class, () -> repository.append(firstTrade()));
        assertArrayEquals(invalidUtf8, Files.readAllBytes(file));
    }

    @Test
    void directoryInsteadOfFileProducesReadAndWriteErrors() throws IOException {
        Files.createDirectory(file);

        assertThrows(IOException.class, repository::findAll);
        assertThrows(IOException.class, () -> repository.append(firstTrade()));
        assertTrue(Files.isDirectory(file));
    }

    @Test
    void missingParentDirectoryProducesWriteErrorWithoutCreatingParents() {
        Path missingParent = directory.resolve("missing");
        TradeHistoryRepository missingParentRepository =
                new TradeHistoryRepository(missingParent.resolve("com/example/trading/csv/trade_history.csv"));

        IOException exception = assertThrows(IOException.class,
                () -> missingParentRepository.append(firstTrade()));

        assertTrue(exception.getMessage().contains("保存できません"));
        assertFalse(Files.exists(missingParent));
    }

    @Test
    void acceptsCrLfFileWithoutChangingExistingRowsOnAppend() throws IOException {
        String original = HEADER + "\r\n" + FIRST_ROW + "\r\n";
        Files.writeString(file, original, StandardCharsets.UTF_8);

        repository.append(secondTrade());

        assertEquals(original + SECOND_ROW + "\n", Files.readString(file));
    }

    private Trade firstTrade() {
        return new Trade(LocalDateTime.of(2026, 10, 7, 9, 15, 32),
                "7203", TradeType.BUY, BigInteger.valueOf(100));
    }

    private Trade secondTrade() {
        return new Trade(LocalDateTime.of(2026, 10, 7, 10, 25, 18),
                "130A", TradeType.SELL, BigInteger.valueOf(25));
    }

    private void assertTrade(Trade expected, Trade actual) {
        assertEquals(expected.getTradeDateTime(), actual.getTradeDateTime());
        assertEquals(expected.getStockCode(), actual.getStockCode());
        assertEquals(expected.getTradeType(), actual.getTradeType());
        assertEquals(expected.getQuantity(), actual.getQuantity());
    }
}
