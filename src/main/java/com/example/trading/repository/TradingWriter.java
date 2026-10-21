package com.example.trading.repository;

import com.example.trading.validator.Trading;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;


public class TradingWriter {

    private final Path filePath;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TradingWriter(Path filePath) {
        this.filePath = filePath;
    }

    public void append(Trading trading) throws IOException {

        String row = "\n" + trading.getTradeDateTime().format(FORMAT) + "," + trading.getStockCode() + "," + trading.getTradeType() + "," + trading.getQuantity() + "," + trading.getPrice();

        Files.writeString(filePath, row, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}