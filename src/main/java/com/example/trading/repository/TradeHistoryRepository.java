package com.example.trading.repository;

import com.example.trading.model.Trade;
import com.example.trading.model.TradeType;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TradeHistoryRepository {

    private final Path filePath;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TradeHistoryRepository(Path filePath) {
        this.filePath = filePath;
    }

    // CSVに保存
    public void append(Trade trade) throws IOException {

        String row = trade.getTradeDateTime().format(FORMAT) + "," + trade.getStockCode() + "," + trade.getTradeType() + "," + trade.getQuantity() + "\n";

        Files.writeString(filePath, row, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    // CSVから読み込む
    public List<Trade> findAll() throws IOException {

        List<Trade> trades = new ArrayList<>();

        if (Files.notExists(filePath)) {
            return trades;
        }

        List<String> lines =
                Files.readAllLines(filePath, StandardCharsets.UTF_8);

        // 0行目はヘッダーなので1行目から読む
        for (int i = 1; i < lines.size(); i++) {

            String[] columns = lines.get(i).split(",");

            LocalDateTime dateTime = LocalDateTime.parse(columns[0], FORMAT);

            String stockCode = columns[1];

            TradeType tradeType = TradeType.valueOf(columns[2]);

            BigInteger quantity = new BigInteger(columns[3]);

            Trade trade = new Trade(dateTime, stockCode, tradeType, quantity);

            trades.add(trade);
        }

        return trades;
    }
}