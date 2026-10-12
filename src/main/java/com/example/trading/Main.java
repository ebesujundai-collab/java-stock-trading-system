package com.example.trading;

import com.example.trading.repository.TradeHistoryRepository;
import com.example.trading.service.TradeHistoryService;
import com.example.trading.service.TradingHoursService;
import com.example.trading.service.TradingService;
import com.example.trading.ui.ConsoleApplication;
import com.example.trading.ui.TradeHistoryTablePrinter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;

/** アプリケーションで使うオブジェクトを組み立て、コンソールを起動する。 */
public class Main {
    public static void main(String[] args) {
        Clock clock = Clock.system(TradingHoursService.JAPAN_ZONE);
        TradeHistoryRepository repository = new TradeHistoryRepository(Path.of("src/main/java/com/example/trading/csv/trade_history.csv"));
        TradingHoursService tradingHoursService = new TradingHoursService();
        TradingService tradingService = new TradingService(repository, tradingHoursService, clock);
        TradeHistoryService historyService = new TradeHistoryService(repository, clock);

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter output = new PrintWriter(System.out, true, StandardCharsets.UTF_8);
        TradeHistoryTablePrinter tablePrinter = new TradeHistoryTablePrinter(output);
        ConsoleApplication application = new ConsoleApplication(
                input, output, tradingService, historyService, tablePrinter);
        application.run();
    }
}
