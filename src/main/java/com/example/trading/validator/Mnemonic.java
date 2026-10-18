package com.example.trading.validator;

import com.example.trading.repository.TradingRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Mnemonic {
    public void menu() {
        Scanner scanner = new Scanner(System.in);
        LocalDateTime dateTime;
        String inputSymbol;
        String inputQuantity;
        String inputPrice;
        TradingRepository repository = new TradingRepository(Path.of(""));
        while(true){
            System.out.println(
                    "メニューを選択してください" + "A" + "B" + "C" + "入力してください"
            );

            String menu = scanner.nextLine();
            if ("A".equals(menu)) {
                System.out.println("約定された取引を開始");

                try { while (true) {
                    System.out.println("日時を入力");
                    String inputHour = scanner.nextLine();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime datetime = LocalDateTime.parse(inputHour,formatter);
                    if (Checks.isValidHour(dateTime)) {
                        break;}
                    System.out.println("妥当な値を入力"); }


                    while (true) {
                        System.out.println("銘柄コード");
                                inputSymbol = scanner.nextLine();
                        if (Checks.isValidSymbol(inputSymbol)){
                            break;}
                        System.out.println("妥当な値を入力"); }


                    System.out.println("売買区分(B/S)");
                    TradeType tradeType = TradeType.fromInput(scanner.nextLine());


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


                    Trading trade =new Trading(
                        dateTime,
                        inputSymbol,
                        tradeType,
                        inputQuantity,
                        inputPrice);
                    repository.append(trade);
                    System.out.println("取引の入力が完了した");

                    } catch (IllegalArgumentException e) {
                    System.out.println("取引できない");
                } catch (IOException e) {
                    System.out.println("保存できません");
                }
            }
        }
    }
}
