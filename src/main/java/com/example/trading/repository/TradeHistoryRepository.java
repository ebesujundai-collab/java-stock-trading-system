package com.example.trading.repository;

import com.example.trading.model.Trade;
import com.example.trading.model.TradeType;
import com.example.trading.validator.QuantityValidator;
import com.example.trading.validator.StockCodeValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** 取引履歴のCSV形式とファイル入出力だけを担当する。 */
public final class TradeHistoryRepository {
    private static final String HEADER = "tradeDateTime,stockCode,side,quantity";
    private static final int COLUMN_COUNT = 4;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss", Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);

    private final Path filePath;

    public TradeHistoryRepository(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "保存先が必要です。");
    }

    /** CSVの順序を変えずに1件を追記する。壊れた既存ファイルには追記しない。 */
    public void append(Trade trade) throws IOException {
        Objects.requireNonNull(trade, "取引データが必要です。");
        String row = toCsvRow(trade);

        try {
            // 既存データを先に検証し、不正な行を残したまま追記しない。
            findAll();

            long fileSize = Files.notExists(filePath) ? 0 : Files.size(filePath);
            StringBuilder addition = new StringBuilder();
            if (fileSize == 0) {
                addition.append(HEADER).append('\n');
            } else if (!endsWithLineBreak()) {
                // 外部エディターで末尾の改行が消されても、別の行として追記する。
                addition.append('\n');
            }
            addition.append(row).append('\n');

            Files.writeString(filePath, addition, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            throw new IOException("取引履歴を保存できません: " + filePath
                    + "（" + exception.getMessage() + "）", exception);
        }
    }

    /** ファイルが存在しない場合と空の場合は、空のListを返す。 */
    public List<Trade> findAll() throws IOException {
        List<Trade> trades = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return trades;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                return trades;
            }
            if (!HEADER.equals(header)) {
                throw invalidCsv(1, "ヘッダーは " + HEADER + " である必要があります。", null);
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                trades.add(parseRow(line, lineNumber));
            }
            return trades;
        } catch (IOException exception) {
            throw new IOException("取引履歴を読み込めません: " + filePath
                    + "（" + exception.getMessage() + "）", exception);
        }
    }

    private Trade parseRow(String line, int lineNumber) throws IOException {
        // -1を指定すると、末尾の空欄も残るため列数と値を正しく検証できる。
        String[] columns = line.split(",", -1);
        if (columns.length != COLUMN_COUNT) {
            throw invalidCsv(lineNumber, "列数は4列である必要があります。", null);
        }

        try {
            LocalDateTime tradeDateTime = LocalDateTime.parse(columns[0], DATE_TIME_FORMATTER);
            String stockCode = StockCodeValidator.validate(columns[1]);
            TradeType tradeType = TradeType.fromInput(columns[2]);
            BigInteger quantity = QuantityValidator.parse(columns[3]);
            return new Trade(tradeDateTime, stockCode, tradeType, quantity);
        } catch (DateTimeParseException exception) {
            throw invalidCsv(lineNumber, "取引日時が不正です。形式: yyyy-MM-dd HH:mm:ss", exception);
        } catch (IllegalArgumentException exception) {
            throw invalidCsv(lineNumber, exception.getMessage(), exception);
        }
    }

    private String toCsvRow(Trade trade) {
        // この4項目にはカンマ・改行・引用符が含まれないため、CSVの引用処理は不要。
        String stockCode = StockCodeValidator.validate(trade.getStockCode());
        BigInteger quantity = QuantityValidator.parse(trade.getQuantity().toString());
        return DATE_TIME_FORMATTER.format(trade.getTradeDateTime()) + ","
                + stockCode + "," + trade.getTradeType().name() + "," + quantity;
    }

    private boolean endsWithLineBreak() throws IOException {
        String contents = Files.readString(filePath, StandardCharsets.UTF_8);
        return contents.endsWith("\n") || contents.endsWith("\r");
    }

    private IOException invalidCsv(int lineNumber, String reason, Exception cause) {
        return new IOException("CSVの" + lineNumber + "行目: " + reason, cause);
    }
}
