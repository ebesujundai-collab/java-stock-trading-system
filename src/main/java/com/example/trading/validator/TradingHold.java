package com.example.trading.validator;

import com.example.trading.repository.TradingWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class  TradingHold {
    public void tradingHold() {
        TradingWriter tradingWriter = new TradingWriter(Path.of("src/main/java/com/example/trading/csv/trading.csv"));
        Validater result = new Validater();
        LocalDateTime dateTime = result.validationDate();
        String inputSymbol = result.validationCode();
        Side side = result.validationSide();
        Long inputQuantity = result.validationQuantity();
        BigDecimal inputPrice = result.validationPrice();
        //ここからwhileまでStep3
//        HistoryType historyType;
//        TradingHistory tradingHistory = new TradingHistory(repository);
//        TradeHistoryTablePrinter printer = new TradeHistoryTablePrinter(new PrintWriter(System.out));


        try {
            Trading trade = new Trading(
                    dateTime,
                    inputSymbol,
                    side,
                    inputQuantity,
                    inputPrice
            );
            tradingWriter.append(trade);
            System.out.println("取引の入力が完了しました");

        } catch (IOException e) {
            System.out.println("保存できません");
        }
    }
}