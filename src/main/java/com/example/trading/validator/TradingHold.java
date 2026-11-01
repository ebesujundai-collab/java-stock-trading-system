package com.example.trading.validator;

import Step4.StockMaster;
import Step4.StockMasterReader;
import com.example.trading.repository.TradingWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;


public class TradingHold {
    public void tradingHold() {

        TradingWriter tradingWriter =
                new TradingWriter(
                        Path.of("src/main/java/com/example/trading/csv/trading.csv")
                );

        StockMasterReader stockMasterReader =
                new StockMasterReader(
                        Path.of("src/main/java/com/example/trading/csv/stock master.csv")
                );

        if (!stockMasterReader.fileExists()) {
            System.out.println("銘柄マスタファイルが存在しません");
            return;
        }

        Validater result = new Validater();

        LocalDateTime dateTime =
                result.validationDate();

        String inputSymbol =
                result.validationCode();

        StockMaster stockMaster =
                stockMasterReader.findBySymbol(inputSymbol);

        while (stockMaster == null) {

            System.out.println(
                    "銘柄マスタに存在しない銘柄コードです"
            );

            inputSymbol =
                    result.validationCode();

            stockMaster =
                    stockMasterReader.findBySymbol(inputSymbol);
        }

        Side side =
                result.validationSide();

        Long inputQuantity =
                result.validationQuantity(stockMaster);

        BigDecimal inputPrice =
                result.validationPrice();

        try {

            Trading trade =
                    new Trading(
                            dateTime,
                            inputSymbol,
                            side,
                            inputQuantity,
                            inputPrice
                    );

            tradingWriter.append(trade);

            System.out.println(
                    "取引の入力が完了しました"
            );

        } catch (IOException e) {

            System.out.println(
                    "保存できません"
            );
        }
    }
}
