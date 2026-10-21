package com.example.trading.repository;

import com.example.trading.validator.Side;
import com.example.trading.validator.Trading;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


    public class TradingReader {

        private final Path filePath;

        private static final DateTimeFormatter FORMAT =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        public TradingReader(Path filePath) {
            this.filePath = filePath;
        }

        public List<Trading> findAll() {

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

                Side side = Side.valueOf(data[2]);

                Long quantity = Long.parseLong(data[3]);

                BigDecimal price = new BigDecimal(data[4]);

                Trading trading = new Trading(
                                tradeDateTime,
                                stockCode,
                                side,
                                quantity,
                                price
                        );
                tradings.add(trading);
            }
            return tradings;
        }
    }