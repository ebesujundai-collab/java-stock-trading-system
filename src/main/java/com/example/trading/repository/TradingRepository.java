package com.example.trading.repository;

import com.example.trading.validator.TradeType;
import com.example.trading.validator.Trading;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TradingRepository {

    private final Path filePath;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TradingRepository(Path filePath) {
        this.filePath = filePath;
    }

    // CSVに保存
    public void append(Trading trading) throws IOException {

        String row = "\n" + trading.getTradeDateTime().format(FORMAT) + "," + trading.getStockCode() + "," + trading.getTradeType() + "," + trading.getQuantity() + "," + trading.getPrice();

        Files.writeString(filePath, row, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    //ここからStep３
    public List<Trading> findAll() throws IOException {

        List<Trading> tradings = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tradings;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        for (String line : lines) {

            if (line.isBlank()) {
                continue;
            }
            if (line.startsWith("tradeDateTime")) {
                continue;
            }

            String[] data = line.split(",");

            LocalDateTime tradeDateTime = LocalDateTime.parse(data[0], FORMAT);

            String stockCode = data[1];

            TradeType tradeType = TradeType.valueOf(data[2]);

            String quantity = data[3];

            String price = data[4];

            Trading trading = new Trading(
                    tradeDateTime,
                    stockCode,
                    tradeType,
                    quantity,
                    price
            );

            tradings.add(trading);
        }

        return tradings;
    }
}