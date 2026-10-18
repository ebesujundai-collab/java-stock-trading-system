package com.example.trading.validator;

import com.example.trading.Step3.TradingHistory;
import com.example.trading.repository.TradingRepository;
import com.example.trading.Step3.HistoryType;
import com.example.trading.Step3.TradeHistoryTablePrinter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Mnemonic {
    public void menu() {
        Scanner scanner = new Scanner(System.in);
        String inputHour;
        LocalDateTime dateTime;
        String inputSymbol;
        TradeType tradeType;
        String inputQuantity;
        String inputPrice;
        TradingRepository repository = new TradingRepository(Path.of("src/main/java/com/example/trading/csv/trading.csv"));
        //ここからwhileまでStep3
        TradingHistory historyService = new TradingHistory(repository);
        TradeHistoryTablePrinter printer = new TradeHistoryTablePrinter(new PrintWriter(System.out));

        while(true){
            System.out.println(
                    "メニューを選択してください" + "A" + "B" + "C" + "入力してください"
            );

            String menu = scanner.nextLine();
            if ("A".equals(menu)) {
                System.out.println("約定された取引を開始");

               while (true) {
                    System.out.println("日時を入力");
                    inputHour = scanner.nextLine();
                    if (Checks.isValidHour(inputHour)) {
                        break;}
                    System.out.println("妥当な値を入力"); }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                dateTime = LocalDateTime.parse(inputHour,formatter);


                    while (true) {
                        System.out.println("銘柄コード");
                                inputSymbol = scanner.nextLine();
                        if (Checks.isValidSymbol(inputSymbol)){
                            break;}
                        System.out.println("妥当な値を入力"); }


                while (true) {
                    System.out.println("売買区分(B/S)");

                    try {
                        tradeType = TradeType.fromInput(scanner.nextLine());
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println("妥当な値を入力");
                    }
                }

                    while (true) {
                        System.out.println("数量");
                        inputQuantity = scanner.nextLine();
                        if (Checks.isValidQuantity(inputQuantity)){
                            break;}
                        System.out.println("妥当な値を入力"); }


                    while (true) {
                        System.out.println("価格");
                        inputPrice = scanner.nextLine();
                        if (Checks.isValidPrice(inputPrice)){
                            break;}
                        System.out.println("妥当な値を入力"); }

                try {
                    Trading trade =new Trading(
                        dateTime,
                        inputSymbol,
                        tradeType,
                        inputQuantity,
                        inputPrice);
                    repository.append(trade);
                    System.out.println("取引の入力が完了した");

                } catch (IOException e) {
                    System.out.println("保存できません");
                }
            }

            //ここからStep3
            else if ("B".equals(menu)) {

                HistoryType historyType;

                while (true) {
                    System.out.println("all / today / yesterday を入力");

                    try {
                        historyType = HistoryType.fromInput(scanner.nextLine());
                        break;

                    } catch (IllegalArgumentException e) {
                        System.out.println("妥当な値を入力");
                    }
                }

                try {
                    List<Trading> tradings =
                            historyService.getHistory(historyType);

                    printer.print(tradings);

                } catch (IOException e) {
                    System.out.println("履歴を読み込めません");
                }
            }
        }
    }
}
