package com.example.trading;

import com.example.trading.Step3.TradingHistoryHold;
import com.example.trading.validator.TradingHold;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws {
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

            } else if ("B".equals(menu)) {
                System.out.println("取引一覧を表示します");

                TradingHistoryHold tradingHistoryHold = new TradingHistoryHold();
                tradingHistoryHold.tradingHistoryHold();

            }
        }
    }
}