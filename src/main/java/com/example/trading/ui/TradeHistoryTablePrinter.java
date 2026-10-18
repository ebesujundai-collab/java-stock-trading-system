package com.example.trading.ui;

import com.example.trading.repository.Trading;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TradeHistoryTablePrinter {

    private final PrintWriter output;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TradeHistoryTablePrinter(PrintWriter output) {
        this.output = output;
    }

    public void print(List<Trading> tradings) {

        if (tradings.isEmpty()) {
            output.println("該当する取引履歴はありません。");
            return;
        }

        output.println("+---------------------+-----------+------+----------+");
        output.println("| tradeDateTime       | stockCode | side | quantity |");

        for (Trading trading : tradings) {

            String dateTime =
                    trading.getTradeDateTime().format(FORMAT);

            String stockCode =
                    trading.getStockCode();

            String side =
                    trading.getTradeType().toString();

            String quantity =
                    trading.getQuantity().toString();

            output.println(
                    "| " + padRight(dateTime, 19)
                            + " | " + padRight(stockCode, 9)
                            + " | " + padRight(side, 4)
                            + " | " + padRight(quantity, 8)
                            + " |"
            );
        }

        output.println("+---------------------+-----------+------+----------+");
    }

    private String padRight(String text, int length) {

        while (text.length() < length) {
            text = text + " ";
        }

        return text;
    }
}