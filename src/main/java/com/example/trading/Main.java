package com.example.trading;

import com.example.trading.model.HistoryPeriod;
import com.example.trading.model.Trade;
import com.example.trading.model.TradeType;
import com.example.trading.repository.TradeHistoryRepository;
import com.example.trading.service.TradeHistoryService;
import com.example.trading.service.TradingHoursService;
import com.example.trading.ui.TradeHistoryTablePrinter;
import com.example.trading.validator.QuantityValidator;
import com.example.trading.validator.StockCodeValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        TradeHistoryRepository repository =
                new TradeHistoryRepository(
                        Path.of("src/main/java/com/example/trading/csv/trade_history.csv")
                );

        TradingHoursService tradingHoursService =
                new TradingHoursService();

        Clock clock =
                Clock.system(TradingHoursService.JAPAN_ZONE);

        TradeHistoryService historyService =
                new TradeHistoryService(repository, clock);

        TradeHistoryTablePrinter tablePrinter =
                new TradeHistoryTablePrinter(
                        new PrintWriter(System.out, true)
                );

        while (true) {

            System.out.println();
            System.out.println("1: 取引");
            System.out.println("2: 履歴");
            System.out.println("0: 終了");
            System.out.print("メニューを選択してください: ");

            String menu = scanner.nextLine();

            if (menu.equals("1")) {

                try {
                    LocalDateTime now =
                            LocalDateTime.now(TradingHoursService.JAPAN_ZONE);

                    tradingHoursService.validate(now);

                    System.out.print("銘柄コード: ");
                    String stockCode =
                            StockCodeValidator.validate(scanner.nextLine());

                    System.out.print("売買区分（BUY / SELL）: ");
                    TradeType tradeType =
                            TradeType.fromInput(scanner.nextLine());

                    System.out.print("株数: ");
                    BigInteger quantity =
                            QuantityValidator.parse(scanner.nextLine());

                    Trade trade = new Trade(
                            now.truncatedTo(ChronoUnit.SECONDS),
                            stockCode,
                            tradeType,
                            quantity
                    );

                    repository.append(trade);

                    System.out.println("取引が成立しました。");

                } catch (IllegalArgumentException e) {
                    System.out.println("取引できません: " + e.getMessage());

                } catch (IOException e) {
                    System.out.println("保存できませんでした: " + e.getMessage());
                }
            }

            else if (menu.equals("2")) {

                try {
                    System.out.print(
                            "履歴の期間（all / today / yesterday）: "
                    );

                    HistoryPeriod period =
                            HistoryPeriod.fromInput(scanner.nextLine());

                    tablePrinter.print(
                            historyService.findByPeriod(period)
                    );

                } catch (IllegalArgumentException e) {
                    System.out.println("入力エラー: " + e.getMessage());

                } catch (IOException e) {
                    System.out.println(
                            "履歴を読み込めませんでした: " + e.getMessage()
                    );
                }
            }

            else if (menu.equals("0")) {
                System.out.println("終了します。");
                break;
            }

            else {
                System.out.println("1、2、0のどれかを入力してください。");
            }
        }
    }
}
