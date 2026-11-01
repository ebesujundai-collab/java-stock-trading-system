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
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    public TradingReader(Path filePath) {
        this.filePath = filePath;
    }

    public boolean fileExists() {
        return Files.exists(filePath);
    }

    public List<Trading> findAll() {

        List<Trading> tradings =
                new ArrayList<>();

        try {

            List<String> lines =
                    Files.readAllLines(
                            filePath,
                            StandardCharsets.UTF_8
                    );

            if (lines.isEmpty()) {
                return tradings;
            }

            List<String> linen =
                    lines.subList(1, lines.size());

            for (String line : linen) {

                String[] data =
                        line.split(",");

                LocalDateTime tradeDateTime =
                        LocalDateTime.parse(
                                data[0],
                                FORMAT
                        );

                String stockCode = data[1];

                Side side =
                        Side.valueOf(data[2]);

                Long quantity =
                        Long.parseLong(data[3]);

                BigDecimal price =
                        new BigDecimal(data[4]);

                Trading trading =
                        new Trading(
                                tradeDateTime,
                                stockCode,
                                side,
                                quantity,
                                price
                        );

                tradings.add(trading);
            }

        } catch (IOException e) {

            return tradings;
        }

        return tradings;
    }
}