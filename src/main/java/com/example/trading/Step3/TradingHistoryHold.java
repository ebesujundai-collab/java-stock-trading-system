package com.example.trading.Step3;

import com.example.trading.repository.TradingReader;
import com.example.trading.validator.Trading;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;


public class TradingHistoryHold {
    public void tradingHistoryHold() {

        Scanner scanner = new Scanner(System.in);
        TradingReader reader = new TradingReader(Path.of("src/main/java/com/example/trading/csv/trading.csv"));
        TradingHistory tradingHistory = new TradingHistory(reader);
        PrintWriter output = new PrintWriter(System.out);
        TradeHistoryTablePrinter printer = new TradeHistoryTablePrinter(output);
        HistoryType historyType;

        while (true) {
            System.out.println("表示する日時を入力してください");
            while (true) {
                try {
                    historyType = HistoryType.fromInput(scanner.nextLine());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("正しい条件を入力してください");
                }
            }
                List<Trading> tradings = tradingHistory.getHistory(historyType);

                if (tradings.isEmpty()) {
                    System.out.println("該当する取引はありません");
                    continue;
                }

                printer.print(tradings);
                break;
            }
        }
    }