package com.example.trading.validator;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class Validater {
    private final Scanner scanner = new Scanner(System.in);

    public LocalDateTime validationDate() {
        while (true) {
            try {
                System.out.println("日時を入力してください");
                String inputHour = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
                LocalDateTime dateTime = LocalDateTime.parse(inputHour, formatter);
                if (Checks.isValidHour(dateTime)) {
                    return dateTime;
                }
                System.out.println("正しい日時を入力してください");
            } catch (DateTimeException e) {
                System.out.println("不正な入力です");
            }
        }
    }

    public String validationCode() {
        while (true) {
            System.out.println("銘柄コード");
            String inputSymbol = scanner.nextLine();
            if (Checks.isValidSymbol(inputSymbol)) {
                return inputSymbol;
            }
            System.out.println("正しい銘柄コードを入力してください");
        }
    }

    public Side validationSide() {
        while (true) {
            System.out.println("売買区分B/S");
            String input = scanner.nextLine();
            if ("B".equals(input)) {
                return Side.B;
            } else if ("S".equals(input)) {
                return Side.S;
            }
            System.out.println("売買区分はSもしくはBを入力してください");
        }
    }

    public Long validationQuantity() {
        while (true) {
            try {
                System.out.println("数量");
                String inputQuantity = scanner.nextLine();
                long quantity = Long.parseLong(inputQuantity);
                if (Checks.isValidQuantity(quantity)) {
                    return quantity;
                }
                System.out.println("正しい数量を入力してください");
            } catch (NumberFormatException e) {
                System.out.println("不正な入力です");
            }
        }
    }

    public BigDecimal validationPrice() {
        while (true) {
            try {
                System.out.println("単価");
                String inputPrice = scanner.nextLine();
                BigDecimal price = new BigDecimal(inputPrice);
                if (Checks.isValidPrice(price)) {
                    return price;
                }
                System.out.println("正しい単価を入力してください");
            } catch (NumberFormatException e) {
                System.out.println("不正な入力です");
            }
        }
    }
}
