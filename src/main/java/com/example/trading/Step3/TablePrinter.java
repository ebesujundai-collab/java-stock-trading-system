package com.example.trading.Step3;

import com.example.trading.validator.Trading;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TablePrinter {


    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,#00.0#");


    public void print(List<Trading> tradings) {

        System.out.println("==============================================================================================");

        System.out.printf(
                "| %-16s | %-5s | %-4s | %-7s | %-10s | %-12s |%n",
                "Traded DateTime",
                "Symbol",
                "Side",
                "Quantity",
                "Unit-price",
                "Amount"
        );

        System.out.println("+------------------+-----------+------+-----------------+---------------+--------------------+");

        for (Trading trading : tradings) {

            String dateTime = trading.getTradeDateTime().format(FORMAT);

            String stockCode = trading.getStockCode();

            String side = trading.getTradeType().getDisplayName();

            String quantity = NUMBER_FORMAT.format(trading.getQuantity());

            String price = PRICE_FORMAT.format(trading.getPrice());

            BigDecimal amount = BigDecimal.valueOf(trading.getQuantity()).multiply(trading.getPrice());

            String amountText = PRICE_FORMAT.format(amount);

            System.out.printf(
                    "| %-16s | %-5s | %-4s | %-7s | %-10s | %-12s |%n",
                    dateTime,
                    stockCode,
                    side,
                    quantity,
                    price,
                    amountText
            );
        }
        System.out.println("+------------------+-----------+------+-----------------+---------------+--------------------+");
    }
}