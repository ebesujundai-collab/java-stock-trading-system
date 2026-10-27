package com.example.trading.Step3;

import com.example.trading.repository.TradingReader;
import com.example.trading.validator.Checks;
import com.example.trading.validator.Trading;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;


public class TradingHistoryHold {
    public void tradingHistoryHold() {

        Scanner scanner = new Scanner(System.in);
        TradingReader reader = new TradingReader(Path.of("src/main/java/com/example/trading/csv/trading.csv"));
        if (!reader.fileExists()) {
            System.out.println("ファイルが存在しません");
            return;
        }
        TradingHistory tradingHistory = new TradingHistory(reader);
        TablePrinter printer = new TablePrinter();


        while (true) {
            System.out.println("表示する日時を入力してください");
            String input = scanner.nextLine();
            List<Trading> tradings;
                try {
                    HistoryType historyType = HistoryType.fromInput(scanner.nextLine());
                    tradings = tradingHistory.getHistory(historyType);
                } catch (IllegalArgumentException e) {
                    if (!Checks.isValidSymbol(input)) {
                        System.out.println("正しい条件を入力してください");
                        continue;
                    }
                    tradings = tradingHistory.getHistoryByStockCode(input);
                }
                printer.print(tradings);
                break;
            }
        }
    }