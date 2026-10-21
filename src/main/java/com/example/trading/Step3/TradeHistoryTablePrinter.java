////ここからStep3
//
//package com.example.trading.Step3;
//
//import com.example.trading.validator.Trading;
//import java.io.PrintWriter;
//import java.math.BigDecimal;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//public class TradeHistoryTablePrinter {
//
//    private final PrintWriter output;
//
//    private static final DateTimeFormatter FORMAT =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//    public TradeHistoryTablePrinter(PrintWriter output) {
//        this.output = output;
//    }
//
//    public void print(List<Trading> tradings) {
//
//        if (tradings.isEmpty()) {
//            output.println("該当する取引履歴はありません。");
//            output.flush();
//            return;
//        }
//
//        output.println(
//                "+------------------+-----------+------+--------------+-----------+---------------+"
//        );
//
//        output.println(
//                "| tradeDateTime    | stockCode | side | quantity     | price     | amount        |"
//        );
//
//        output.println(
//                "+------------------+-----------+------+--------------+-----------+---------------+"
//        );
//
//        for (Trading trading : tradings) {
//
//            String dateTime = trading.getTradeDateTime().format(FORMAT);
//
//            String stockCode = trading.getStockCode();
//
//            String side = trading.getTradeType().toString();
//
//            String quantity = trading.getQuantity();
//
//            String price = trading.getPrice();
//
//            BigDecimal amount = new BigDecimal(quantity).multiply(new BigDecimal(price));
//
//            String amountText = amount.toPlainString();
//
//            output.println(
//                    "| " + padRight(dateTime, 16)
//                            + " | " + padRight(stockCode, 9)
//                            + " | " + padRight(side, 4)
//                            + " | " + padRight(quantity, 12)
//                            + " | " + padRight(price, 9)
//                            + " | " + padRight(amountText, 13)
//                            + " |"
//            );
//        }
//
//        output.println("+------------------+-----------+------+--------------+-----------+---------------+");
//
//        output.flush();
//    }
//
//    private String padRight(String text, int length) {
//
//        while (text.length() < length) {
//            text = text + " ";
//        }
//
//        return text;
//    }
//}