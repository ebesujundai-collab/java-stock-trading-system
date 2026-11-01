package com.example.trading.Step3;

import Step4.StockMaster;
import Step4.StockMasterReader;
import com.example.trading.validator.Trading;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TablePrinter {


    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,#00.0#");

    private final StockMasterReader stockMasterReader;

    public TablePrinter(StockMasterReader stockMasterReader) {
        this.stockMasterReader = stockMasterReader;
    }


    public void print(List<Trading> tradings) {

        System.out.println("==============================================================================================");

        System.out.printf(
                "| %-16s | %-5s | %-4s | %-7s | %-10s | %-12s |%n",
                "Traded DateTime",
                "Product",
                "Side",
                "Quantity",
                "Unit-price",
                "Amount"
        );

        System.out.println("+------------------+-----------+------+-----------------+---------------+--------------------+");

        for (Trading trading : tradings) {

            String dateTime = trading.getTradeDateTime().format(FORMAT);

            StockMaster stockMaster = stockMasterReader.findBySymbol(trading.getStockCode());

            String productName = "";

            if (stockMaster != null) {
                productName = stockMaster.getProductName();
            }

            if (productName.length() > 20) {
                productName = productName.substring(0, 17) + "...";
            }

            String product = trading.getStockCode() + " " + productName;

            String side = trading.getTradeType().getDisplayName();

            String quantity = NUMBER_FORMAT.format(trading.getQuantity());

            String price = PRICE_FORMAT.format(trading.getPrice());

            BigDecimal amount = BigDecimal.valueOf(trading.getQuantity()).multiply(trading.getPrice());

            String amountText = PRICE_FORMAT.format(amount);

            System.out.printf(
                    "| %-16s | %-5s | %-4s | %-7s | %-10s | %-12s |%n",
                    dateTime,
                    product,
                    side,
                    quantity,
                    price,
                    amountText
            );
        }
        System.out.println("+------------------+-----------+------+-----------------+---------------+--------------------+");
    }
}