package com.example.trading.validator;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class Checks {
    private Checks() {
    }

    public static boolean isValidSymbol(String str) {
        String code = str;
        if (code.length() != 4) {
            return false;
        } else {
            char a = code.charAt(0);
            if (a < '0' || '9' < a) {
                return false;
            } else {
                char c = code.charAt(2);
                if (c < '0' || '9' < c) {
                    return false;
                } else {
                    {
                        for (int i = 0; i < 4; i++) {
                            char z = code.charAt(i);
                            String forbidden = "BEIOQVZabcdefghijklmnopqrstuvwxyz";
                            if (forbidden.indexOf(z) >= 0) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
    }

    public static boolean isValidHour(String inputHour) {
        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDateTime date =
                    LocalDateTime.parse(inputHour, formatter);

            DayOfWeek day = date.getDayOfWeek();
            if (DayOfWeek.SATURDAY == day || DayOfWeek.SUNDAY == day) {
                return false;
            }
            LocalDateTime now = LocalDateTime.now();
            if (date.isAfter(now)) {
                return false;
            }
            LocalTime time1 = date.toLocalTime();
            LocalTime time2 = LocalTime.of(9, 00);
            LocalTime time3 = LocalTime.of(11, 30);
            if (!time1.isBefore(time2) && !time1.isAfter(time3)) {
                return true;
            }
            LocalTime time4 = LocalTime.of(12, 30);
            LocalTime time5 = LocalTime.of(15, 30);
            if (!time1.isBefore(time4) && !time1.isAfter(time5)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidPrice(String prices) {
        try {
            double price = Double.parseDouble(prices);

            if (price <= 0) {
                return false;
            }
            if (price > 999999999) {
                return false;
            }
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isValidQuantity(String quantitys) {
        try {
            long quantity = Long.parseLong(quantitys);

            if (quantity <= 0) {
                return false;
            }
            if (quantity % 100 != 0) {
                return false;
            }
            if (quantitys.length() > 12) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
