package com.example.trading.Step3;

import com.example.trading.validator.Trading;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TradeHistoryTablePrinter {

    private final PrintWriter output;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final DecimalFormat NUMBER_FORMAT =
            new DecimalFormat("#,##0.0#");

    public TradeHistoryTablePrinter(PrintWriter output) {
        this.output = output;
    }

    public void print(List<Trading> tradings) {

        if (tradings.isEmpty()) {
            output.println("該当する取引はありません");
            output.flush();
            return;
        }

        output.println(
                "+------------------+-----------+------+-----------------+---------------+--------------------+"
        );

        output.printf(
                "| %-16s | %-9s | %-4s | %15s | %13s | %18s |%n",
                "tradeDateTime",
                "stockCode",
                "side",
                "quantity",
                "price",
                "amount"
        );

        output.println(
                "+------------------+-----------+------+-----------------+---------------+--------------------+"
        );

        for (Trading trading : tradings) {

            String dateTime =
                    trading.getTradeDateTime().format(FORMAT);

            String stockCode =
                    trading.getStockCode();

            String side =
                    trading.getTradeType().getDisplayName();

            String quantity =
                    String.format("%,d", trading.getQuantity());

            String price =
                    NUMBER_FORMAT.format(trading.getPrice());

            BigDecimal amount =
                    BigDecimal.valueOf(trading.getQuantity())
                            .multiply(trading.getPrice());

            String amountText =
                    NUMBER_FORMAT.format(amount);

            output.printf(
                    "| %-16s | %-9s | %-4s | %15s | %13s | %18s |%n",
                    dateTime,
                    stockCode,
                    side,
                    quantity,
                    price,
                    amountText
            );
        }

        output.println(
                "+------------------+-----------+------+-----------------+---------------+--------------------+"
        );

        output.flush();
    }
}