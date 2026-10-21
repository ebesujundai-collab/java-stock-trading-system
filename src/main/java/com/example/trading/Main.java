package com.example.trading;

import com.example.trading.validator.Trading;
import com.example.trading.validator.TradingHold;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("操作するメニューを選んでください\n" +
                    "A　：取引の入力\n" +
                    "B　：取引一覧の表示\n" +
                    "Q　：アプリの終了\n" +
                    "入力してください"
            );

            String menu = scanner.nextLine();
            if ("A".equals(menu)) {
                System.out.println("約定された取引を開始します");

                TradingHold tradingHold = new TradingHold();
                tradingHold.tradingHold();
            }
        }
    }
}

//             else if ("B".equals(menu)) {
//                    while (true) {
//                        System.out.println("all / today / yesterday を入力");
//                        try {historyType = HistoryType.fromInput(scanner.nextLine());
//                            break;
//                        } catch (IllegalArgumentException e) {
//                            System.out.println("妥当な値を入力");}}
//
//                    try {
//                        List<Trading> tradings = tradingHistory.getHistory(historyType);
//                        printer.print(tradings);
//                    } catch (IOException e) {System.out.println("履歴を読み込めません");}}}}}