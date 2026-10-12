package com.example.trading.ui;

import com.example.trading.model.HistoryPeriod;
import com.example.trading.model.TradeType;
import com.example.trading.service.TradeHistoryService;
import com.example.trading.service.TradingService;
import com.example.trading.validator.QuantityValidator;
import com.example.trading.validator.StockCodeValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

/** 入力の案内、再入力、結果の表示を受け持つ。取引条件やCSV処理はサービスに任せる。 */
public class ConsoleApplication {
    private final BufferedReader input;
    private final PrintWriter output;
    private final TradingService tradingService;
    private final TradeHistoryService historyService;
    private final TradeHistoryTablePrinter tablePrinter;

    public ConsoleApplication(BufferedReader input, PrintWriter output,
                              TradingService tradingService, TradeHistoryService historyService,
                              TradeHistoryTablePrinter tablePrinter) {
        this.input = input;
        this.output = output;
        this.tradingService = tradingService;
        this.historyService = historyService;
        this.tablePrinter = tablePrinter;
    }

    public void run() {
        output.println("日本株模擬取引システム");
        boolean running = true;
        while (running) {
            try {
                printMenu();
                String selection = readInput("メニューを選択してください: ");
                if (selection == null) {
                    break;
                }

                switch (selection) {
                    case "1" -> running = enterTrade();
                    case "2" -> running = showHistory();
                    case "0" -> running = false;
                    default -> output.println("入力エラー: 1、2、0のいずれかを入力してください。");
                }
            } catch (IOException exception) {
                output.println("入力を読み込めませんでした: " + exception.getMessage());
                break;
            }
        }
        output.println("終了します。");
        output.flush();
    }

    private void printMenu() {
        output.println();
        output.println("1: 取引");
        output.println("2: 履歴");
        output.println("0: 終了");
    }

    /** EOFならfalseを返し、途中の入力を取引として保存せずに終了する。 */
    private boolean enterTrade() throws IOException {
        String stockCode = readStockCode();
        if (stockCode == null) {
            return false;
        }

        TradeType tradeType = readTradeType();
        if (tradeType == null) {
            return false;
        }

        BigInteger quantity = readQuantity();
        if (quantity == null) {
            return false;
        }

        try {
            tradingService.execute(stockCode, tradeType, quantity);
            output.println("取引が成立しました。CSVに保存しました。");
        } catch (IllegalArgumentException exception) {
            output.println("取引できません: " + exception.getMessage());
        } catch (IOException exception) {
            output.println("取引を保存できませんでした: " + exception.getMessage());
        }
        return true;
    }

    private boolean showHistory() throws IOException {
        HistoryPeriod period = readHistoryPeriod();
        if (period == null) {
            return false;
        }
        try {
            tablePrinter.print(historyService.findByPeriod(period));
        } catch (IOException exception) {
            output.println("履歴を読み込めませんでした: " + exception.getMessage());
        }
        return true;
    }

    private String readStockCode() throws IOException {
        while (true) {
            String value = readInput("銘柄コード（4文字）: ");
            if (value == null) {
                return null;
            }
            try {
                return StockCodeValidator.validate(value);
            } catch (IllegalArgumentException exception) {
                printInputError(exception);
            }
        }
    }

    private TradeType readTradeType() throws IOException {
        while (true) {
            String value = readInput("売買区分（BUY / SELL）: ");
            if (value == null) {
                return null;
            }
            try {
                return TradeType.fromInput(value);
            } catch (IllegalArgumentException exception) {
                printInputError(exception);
            }
        }
    }

    private BigInteger readQuantity() throws IOException {
        while (true) {
            String value = readInput("株数（1以上の整数）: ");
            if (value == null) {
                return null;
            }
            try {
                return QuantityValidator.parse(value);
            } catch (IllegalArgumentException exception) {
                printInputError(exception);
            }
        }
    }

    private HistoryPeriod readHistoryPeriod() throws IOException {
        while (true) {
            String value = readInput("履歴の期間（all / today / yesterday）: ");
            if (value == null) {
                return null;
            }
            try {
                return HistoryPeriod.fromInput(value);
            } catch (IllegalArgumentException exception) {
                printInputError(exception);
            }
        }
    }

    private String readInput(String prompt) throws IOException {
        output.print(prompt);
        output.flush();
        return input.readLine();
    }

    private void printInputError(IllegalArgumentException exception) {
        output.println("入力エラー: " + exception.getMessage());
    }
}
